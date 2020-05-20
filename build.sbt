ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "io.kirill"
ThisBuild / organizationName := "kirill"

lazy val root = (project in file("."))
  .settings(
    name := "crypto-tracker"
  )
  .aggregate(core)

lazy val dockerSettings = Seq(
  packageName in Docker := "crypto-tracker",
  version in Docker := sys.env.getOrElse("APP_VERSION", version.value),
  dockerBaseImage := "openjdk:11.0.4-jre-slim",
  dockerExposedPorts ++= Seq(8080),
  dockerUpdateLatest := true,
  makeBatScripts := Seq()
)

lazy val core = (project in file("modules/core"))
  .enablePlugins(DockerPlugin, AshScriptPlugin)
  .settings(dockerSettings)
  .settings(
    name := "crypto-tracker-core",
    scalacOptions += "-Ymacro-annotations",
    scalafmtOnCompile := true,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    Defaults.itSettings,
    libraryDependencies ++= Dependencies.core ++ Dependencies.test
  )
