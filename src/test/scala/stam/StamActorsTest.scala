package stam

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.collection.parallel.CollectionConverters._

class StamActorsTest extends AnyFlatSpec with Matchers with ScalaFutures with ScalatestRouteTest {
  lazy val routes = StamActors.stamRoutes

  "test in url" should "run" in {
    val data = List.range(0, 10).map(i => {
      () => Get(s"/api/akka/actor/cache?key=key-${i%2}") ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        val r = Await.result(Unmarshal(response).to[String], 10.seconds)
        println(s"Received value ${r}")
      }
    })
    data.par.foreach(f => f())
    Thread.sleep(1000L)
    data.par.foreach(f => f())
  }

  "test in body" should "run" in  {
    val data = List.range(0, 10).map(i => {
      val rs = s"${i%2}"
      () => Post("/api/akka/http/cache", rs) ~> routes ~> check {
        status should === (StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        val r = Await.result(Unmarshal(response).to[String], 10.seconds)
        println(s"Received value ${r}")
      }
    })
    data.par.foreach(f => f())
    Thread.sleep(1000L)
    data.par.foreach(f => f())
  }
}
