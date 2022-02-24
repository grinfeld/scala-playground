name := "stam"

version := "0.1"

scalaVersion := "2.13.1"

val versions = new {
  val akkaVersion = "2.6.18"
  val akkaHttpVersion = "10.2.8"
  val tests = new {
    val scalaTest = "3.2.11"
    val mockito = "1.17.0"
  }
  val log4j = "2.17.1"
  val parVersion = "1.0.4"
}

lazy val root = (project in file("."))
  .settings(
    name := "spark-scala",
    compileOrder:= CompileOrder.JavaThenScala,
    resolvers += Resolver.mavenLocal,
    resolvers += Resolver.jcenterRepo,
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parallel-collections" % versions.parVersion,
      "com.typesafe.akka" %% "akka-http" % versions.akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-caching" % versions.akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % versions.akkaVersion,
      "com.typesafe.akka" %% "akka-actor" % versions.akkaVersion,
      "org.apache.logging.log4j" % "log4j-core" % versions.log4j,
      "org.apache.logging.log4j" % "log4j-api" % versions.log4j,
      "com.typesafe.akka" %% "akka-http-testkit" % versions.akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % versions.akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % versions.akkaVersion % Test,
      "org.scalatest" %% "scalatest" % versions.tests.scalaTest % Test,
      "org.mockito" %% "mockito-scala-scalatest" % versions.tests.mockito % Test
    )
  )