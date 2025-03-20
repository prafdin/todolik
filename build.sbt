import sbt.Package.ManifestAttributes

val applicationVersion = "0.1.0-SNAPSHOT"

ThisBuild / version := applicationVersion

ThisBuild / scalaVersion := "3.3.5"

packageOptions := Seq(
    ManifestAttributes(
        ("Todolik-Version", applicationVersion)
    )
)

bspEnabled := true
enablePlugins(JavaAppPackaging)
enablePlugins(DebianPlugin)

Debian / maintainer := "https://github.com/prafdin"
Debian / packageSummary := "TODOlik - a small cli program for manage your TODOs"
// One space is required for all lines in packageDescription except first line
Debian / packageDescription :=
    """|Application with CLI for control TODO notes.
       | This is application was created just for fun, so do not expect anything interesting here.
       |""".stripMargin


ThisBuild / assemblyMergeStrategy := {
    case PathList("module-info.class") => MergeStrategy.discard
    case PathList("META-INF", "versions", xs@_, "module-info.class") => MergeStrategy.discard
    case _ => MergeStrategy.first
}

ThisBuild / resolvers +=  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/"

val typesafeConfigDependency = "com.typesafe" % "config" % "1.4.3"
val circleDependency = Seq(
    "io.circe" %% "circe-core" % "0.14.3",
    "io.circe" %% "circe-generic" % "0.14.3",
    "io.circe" %% "circe-parser" % "0.14.3",
)
val jcabiDependency = Seq(
    "com.jcabi" % "jcabi-manifests" % "2.1.0",
)

lazy val root = (project in file("."))
    .enablePlugins(JavaAppPackaging)
    .settings(
        name := "todolik",
        idePackagePrefix := Some("ru.prafdin.todolik"),
        libraryDependencies += typesafeConfigDependency,
        libraryDependencies ++= circleDependency,
        libraryDependencies ++= jcabiDependency
    )
