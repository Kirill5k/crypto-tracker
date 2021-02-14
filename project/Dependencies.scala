import sbt._

object Dependencies {

  object Versions {
    val mongo4cats     = "0.1.5"
    val http4s         = "0.21.18"
    val sttp           = "3.0.0"
    val circe          = "0.13.0"
    val pureConfig     = "0.12.3"
    val squants        = "1.6.0"

    val log4cats = "1.0.1"
    val logback  = "1.2.3"

    val scalaTest     = "3.2.3"
    val mockito       = "1.14.0"
    val embeddedMongo = "2.2.0"
  }

  object Libraries {
    lazy val mongo4cats = "io.github.kirill5k" %% "mongo4cats-core" % Versions.mongo4cats

    object logging {
      lazy val logback  = "ch.qos.logback"     % "logback-classic" % Versions.logback
      lazy val log4cats = "io.chrisdavenport" %% "log4cats-slf4j"  % Versions.log4cats

      lazy val all = Seq(log4cats, logback)
    }

    object pureconfig {
      lazy val core       = "com.github.pureconfig" %% "pureconfig"             % Versions.pureConfig
      lazy val catsEffect = "com.github.pureconfig" %% "pureconfig-cats-effect" % Versions.pureConfig

      lazy val all = Seq(core, catsEffect)
    }

    object sttp {
      lazy val core        = "com.softwaremill.sttp.client3" %% "core"                           % Versions.sttp
      lazy val circe       = "com.softwaremill.sttp.client3" %% "circe"                          % Versions.sttp
      lazy val catsBackend = "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % Versions.sttp

      lazy val all = Seq(core, circe, catsBackend)
    }

    object http4s {
      lazy val core   = "org.http4s" %% "http4s-core"         % Versions.http4s
      lazy val dsl    = "org.http4s" %% "http4s-dsl"          % Versions.http4s
      lazy val server = "org.http4s" %% "http4s-server"       % Versions.http4s
      lazy val blaze  = "org.http4s" %% "http4s-blaze-server" % Versions.http4s
      lazy val circe  = "org.http4s" %% "http4s-circe"        % Versions.http4s

      lazy val all = Seq(core, dsl, server, blaze, circe)
    }

    object circe {
      lazy val core          = "io.circe" %% "circe-core"           % Versions.circe
      lazy val literal       = "io.circe" %% "circe-literal"        % Versions.circe
      lazy val generic       = "io.circe" %% "circe-generic"        % Versions.circe
      lazy val genericExtras = "io.circe" %% "circe-generic-extras" % Versions.circe
      lazy val parser        = "io.circe" %% "circe-parser"         % Versions.circe

      lazy val all = Seq(core, literal, generic, genericExtras, parser)
    }

    lazy val squants       = "org.typelevel"      %% "squants"                   % Versions.squants
    lazy val scalaTest     = "org.scalatest"      %% "scalatest"                 % Versions.scalaTest
    lazy val embeddedMongo = "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % Versions.embeddedMongo

    object mockito {
      val core      = "org.mockito" %% "mockito-scala"           % Versions.mockito
      val scalatest = "org.mockito" %% "mockito-scala-scalatest" % Versions.mockito
    }
  }

  lazy val core = Seq(
    Libraries.squants,
    Libraries.mongo4cats
  ) ++
    Libraries.circe.all ++
    Libraries.http4s.all ++
    Libraries.logging.all ++
    Libraries.sttp.all ++
    Libraries.pureconfig.all

  lazy val test = Seq(
    Libraries.scalaTest         % Test,
    Libraries.mockito.core      % Test,
    Libraries.mockito.scalatest % Test,
    Libraries.embeddedMongo     % Test
  )
}
