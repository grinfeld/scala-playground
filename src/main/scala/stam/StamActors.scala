package stam

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.caching.LfuCache
import akka.http.caching.scaladsl.{Cache, CachingSettings}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Random, Success}

object StamActors extends App {

  private implicit lazy val routesTimeout: Timeout = Timeout(60.seconds)

  lazy val cacheMap = scala.collection.mutable.HashMap[String, Either[String, Future[String]]]()

  lazy val system: ActorSystem = ActorSystem("stamTest")

  lazy  val cacheFacadeActor: ActorRef = getSystem().actorOf(Props[CacheFacadeActor], "cacheFacade")
  lazy  val LongOpActor: ActorRef = getSystem().actorOf(Props[LongOperationActor], "ptbb")

  def getSystem(): ActorSystem = ActorSystem("stamTest")

  def getCacheFaced(): ActorRef = cacheFacadeActor

  def getPtbbEndpoint(): ActorRef = LongOpActor

  lazy val lfuCache: Cache[String, String] = {
    val defaultCachingSettings = CachingSettings(getSystem())
    LfuCache(defaultCachingSettings
      .withLfuCacheSettings(defaultCachingSettings.lfuCacheSettings
        .withInitialCapacity(25)
        .withMaxCapacity(50)
        .withTimeToLive(20.seconds)
        .withTimeToIdle(10.seconds))
    )
  }

  def getLfuCache(): Cache[String, String] = lfuCache

  lazy val stamRoutes: Route = {

    pathPrefix("api") {
      // using akk http cache with coffeine
      path("akka" / "http" / "cache") {
        post {
          entity(as[String]) { keyIsBody =>
            val ft = getLfuCache().get(keyIsBody, () => {
              println(s"getting data from cache for key $keyIsBody (${Thread.currentThread().getName})")
              "stam-" + new Random().nextInt(100)
            })
            onComplete(ft) {
              case Success(r) =>
                complete(HttpResponse(status = StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, r)))
              case scala.util.Failure(t) =>
                t.printStackTrace()
                complete(HttpResponse(status = StatusCodes.BadRequest, entity = HttpEntity(ContentTypes.`application/json`, t.getMessage)))
            }
          }
        }
      } ~
      // using some cache via additional actor
      path("akka" / "actor" / "cache") {
        get {
          parameters("key") { key =>
            onComplete((getCacheFaced() ? Request(key)).mapTo[String]) {
              case Success(r) =>
                complete(HttpResponse(status = StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, r)))
              case scala.util.Failure(t) =>
                t.printStackTrace()
                complete(HttpResponse(status = StatusCodes.BadRequest, entity = HttpEntity(ContentTypes.`application/json`, t.getMessage)))
            }
          }
        }
      }
    }
  }

  class CacheFacadeActor extends Actor {
    override def receive: Receive = {
      case Request(key) =>
        val http = sender()

        // prints for tests
        cacheMap.get(key) match {
          case None => println(s"Nothing for $key")
          case Some(either) => either match {
            case Left(str) => println(s"found in cache $key with $str (${Thread.currentThread().getName})")
            case Right(ft) => println(s"waiting for future for $key (${Thread.currentThread().getName})")
          }
        }
        cacheMap.getOrElseUpdate(key, Right((getPtbbEndpoint() ? key).mapTo[String].map(receivedValue => {
          cacheMap.put(key, Left(receivedValue))
          receivedValue
        }))) match {
          case Left(foundValue) =>
            http ! foundValue
          case Right(ft) =>
            ft.andThen {
              case scala.util.Failure(t) => throw t
              case Success(value) =>
                println(s"Future finished for $key with $value (${Thread.currentThread().getName})")
                http ! value
                value
            }
        }
    }
  }

  class LongOperationActor extends Actor {
    override def receive: Receive = {
      case key: String =>
        sender ! "stam-" + new Random().nextInt(100)
    }
  }

  case class Request(value: String)
}
