package kirill5k.cryptotracker.common

import cats.effect.{Blocker, IO}
import kirill5k.cryptotracker.CatsIOSpec
import kirill5k.cryptotracker.common.config.AppConfig

class AppConfigSpec extends CatsIOSpec {

  "An AppConfig" should {
    "should load itself without errors" in {
      Blocker[IO].use(AppConfig.load[IO]).unsafeToFuture().map { config =>
        config.coinlib.baseUri mustBe "foo"
        config.telegram.baseUri mustBe "bar"
      }
    }
  }
}
