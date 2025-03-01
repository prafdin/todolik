ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

bspEnabled := true

val typesafeConfigDependency = "com.typesafe" % "config" % "1.4.3"
val circleDependency = Seq(
    "io.circe" %% "circe-core" % "0.14.3",
    "io.circe" %% "circe-generic" % "0.14.3",
    "io.circe" %% "circe-parser" % "0.14.3"
)

lazy val root = (project in file("."))
    .enablePlugins(JavaAppPackaging)
    .settings(
        name := "todolik",
        idePackagePrefix := Some("ru.prafdin.todolik"),
        libraryDependencies += typesafeConfigDependency,
        libraryDependencies ++= circleDependency
    )
