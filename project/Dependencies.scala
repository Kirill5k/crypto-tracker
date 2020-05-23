import sbt._

object Dependencies {

  object Versions {
    val catsCore       = "2.1.1"
    val catsEffect     = "2.1.3"
    val catsEffectTest = "0.4.0"
    val fs2            = "2.3.0"
    val sttp           = "2.1.4"
    val circe          = "0.13.0"

    val log4cats = "1.0.1"
    val logback  = "1.2.3"

    val scalaTest = "3.1.1"
    val mockito   = "1.14.0"
  }

  object Libraries {
    object cats {
      lazy val core   = "org.typelevel" %% "cats-core"   % Versions.catsCore
      lazy val effect = "org.typelevel" %% "cats-effect" % Versions.catsEffect
      lazy val effectTest = "com.codecommit" %% "cats-effect-testing-scalatest" % Versions.catsEffectTest
    }

    val logback  = "ch.qos.logback"    % "logback-classic" % Versions.logback
    val log4cats = "io.chrisdavenport" %% "log4cats-slf4j" % Versions.log4cats

    lazy val fs2Core = "co.fs2" %% "fs2-core" % Versions.fs2

    object sttp {
      lazy val core  = "com.softwaremill.sttp.client" %% "core"                           % Versions.sttp
      lazy val circe = "com.softwaremill.sttp.client" %% "circe"                          % Versions.sttp
      lazy val cats  = "com.softwaremill.sttp.client" %% "async-http-client-backend-cats" % Versions.sttp
    }

    object circe {
      lazy val core    = "io.circe" %% "circe-core"           % Versions.circe
      lazy val generic = "io.circe" %% "circe-generic"        % Versions.circe
      lazy val parser  = "io.circe" %% "circe-parser"         % Versions.circe
      lazy val extras  = "io.circe" %% "circe-generic-extras" % Versions.circe
    }

    lazy val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest

    object mockito {
      val core      = "org.mockito" %% "mockito-scala"           % Versions.mockito
      val scalatest = "org.mockito" %% "mockito-scala-scalatest" % Versions.mockito
    }
  }

  lazy val core = Seq(
    Libraries.cats.core,
    Libraries.cats.effect,
    Libraries.logback,
    Libraries.log4cats,
    Libraries.fs2Core,
    Libraries.circe.core,
    Libraries.circe.extras,
    Libraries.circe.generic,
    Libraries.circe.parser,
    Libraries.sttp.core,
    Libraries.sttp.circe,
    Libraries.sttp.cats
  )

  lazy val test = Seq(
    Libraries.scalaTest         % Test,
    Libraries.cats.effectTest   % Test,
    Libraries.mockito.core      % Test,
    Libraries.mockito.scalatest % Test
  )
}
