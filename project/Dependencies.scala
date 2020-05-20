import sbt._

object Dependencies {

  object Versions {
    val catsCore       = "2.1.1"
    val catsEffect     = "2.1.3"
    val catsEffectTest = "0.4.0"

    val scalaTest = "3.1.1"
    val mockito   = "1.14.0"
  }

  object Libraries {
    object cats {
      lazy val core   = "org.typelevel" %% "cats-core"   % Versions.catsCore
      lazy val effect = "org.typelevel" %% "cats-effect" % Versions.catsEffect

      lazy val effectTest = "com.codecommit" %% "cats-effect-testing-scalatest" % Versions.catsEffectTest
    }

    lazy val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest

    object mockito {
      val core      = "org.mockito" %% "mockito-scala"           % Versions.mockito
      val scalatest = "org.mockito" %% "mockito-scala-scalatest" % Versions.mockito
    }
  }

  lazy val core = Seq(
    Libraries.cats.core,
    Libraries.cats.effect
  )

  lazy val test = Seq(
    Libraries.scalaTest         % Test,
    Libraries.cats.effectTest   % Test,
    Libraries.mockito.core      % Test,
    Libraries.mockito.scalatest % Test
  )
}
