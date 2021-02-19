import com.typesafe.sbt.packager.docker._

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.1.2"
ThisBuild / organization := "io.github.kirill5k"
ThisBuild / organizationName := "kirill5k"

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  publish / skip := true
)

lazy val docker = Seq(
  packageName := moduleName.value,
  version := version.value,
  maintainer := "immotional@aol.com",
  dockerBaseImage := "adoptopenjdk/openjdk15-openj9:alpine-jre",
  dockerUpdateLatest := true,
  Docker / maintainer := "kirill5k",
  dockerRepository := Some("us.gcr.io"),
  makeBatScripts := List(),
  dockerCommands := {
    val commands         = dockerCommands.value
    val (stage0, stage1) = commands.span(_ != DockerStageBreak)
    val (before, after)  = stage1.splitAt(4)
    val installBash      = Cmd("RUN", "apk update && apk upgrade && apk add bash")
    stage0 ++ before ++ List(installBash) ++ after
  }
)

lazy val root = (project in file("."))
  .settings(noPublish)
  .settings(
    name := "crypto-tracker"
  )
  .aggregate(core)

lazy val core = (project in file("modules/core"))
  .enablePlugins(JavaAppPackaging, JavaAgent, DockerPlugin)
  .settings(docker)
  .settings(
    name := "crypto-tracker-core",
    moduleName := "crypto-tracker-core",
    packageSummary := "Crypto tracker",
    packageDescription := "Package description",
    Docker / packageName := "crypto-tracker-2020/core",
    libraryDependencies ++= Dependencies.core ++ Dependencies.test
  )
