package kirill5k.cryptotracker.common

import cats.effect.{Blocker, IO}
import kirill5k.cryptotracker.CatsIOSpec
import kirill5k.cryptotracker.common.config.AppConfig

class AppConfigSpec extends CatsIOSpec {

  System.setProperty("MONGO_USER", "user")
  System.setProperty("MONGO_PASSWORD", "host")
  System.setProperty("MONGO_HOST", "mongodb-host")

  "An AppConfig" should {
    "should load itself without errors" in {
      Blocker[IO].use(AppConfig.load[IO]).unsafeToFuture().map { config =>
        config.reddit.baseUri mustBe "https://api.pushshift.io"
        config.telegram.baseUri mustBe "https://api.telegram.org"
        config.mongo.connectionUri mustBe "mongodb+srv://user:host@mongodb-host/crypto-tracker"
      }
    }
  }
}
