import Dependencies._

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / organization := "ru.arlen"

lazy val root = (project in file("."))
  .settings(
    name := "BankAccount",
    libraryDependencies ++= Seq(
      guice,
      scalaTest,
      h2
    ) ++ circe ++ slick
  )
  .enablePlugins(PlayScala)