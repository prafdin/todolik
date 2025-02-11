ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

bspEnabled := true

lazy val root = (project in file("."))
  .settings(
    name := "todolik",
    idePackagePrefix := Some("ru.prafdin.todolik")
  )
