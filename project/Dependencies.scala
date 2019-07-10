import sbt._

object Dependencies {
  val circeVersion = "0.10.0"
  lazy val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  lazy val h2 = "com.h2database" % "h2" % "1.4.197"
  lazy val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test
  lazy val slick = Seq(
    "com.typesafe.play" %% "play-slick" % "4.0.1",
    "com.typesafe.play" %% "play-slick-evolutions" % "4.0.1"
  )
}
