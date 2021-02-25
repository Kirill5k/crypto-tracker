package kirill5k.cryptotracker.common

import cats.effect.{Blocker, IO}
import kirill5k.cryptotracker.CatsIOSpec
import kirill5k.cryptotracker.common.config.AppConfig

class AppConfigSpec extends CatsIOSpec {

  System.setProperty("MONGO_USER", "user")
  System.setProperty("MONGO_PASSWORD", "host")
  System.setProperty("MONGO_HOST", "mongodb-host")
  System.setProperty("ALPHA_VANTAGE_API_KEY", "av-api-key")

  "An AppConfig" should {
    "should load itself without errors" in {
      Blocker[IO].use(AppConfig.load[IO]).unsafeToFuture().map { config =>
        config.reddit.pushshiftUri mustBe "https://api.pushshift.io"
        config.telegram.baseUri mustBe "https://api.telegram.org"
        config.mongo.connectionUri mustBe "mongodb+srv://user:host@mongodb-host/crypto-tracker"
        config.alphaVantage.apiKey mustBe "av-api-key"
      }
    }
  }
}
