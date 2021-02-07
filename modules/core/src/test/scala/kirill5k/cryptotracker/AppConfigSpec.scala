package kirill5k.cryptotracker

import kirill5k.cryptotracker.config.AppConfig
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AppConfigSpec extends AnyWordSpec with Matchers {

  "An AppConfig" should {
    "should load itself without errors" in {
      val config = AppConfig.load

      config.coinlib.baseUri mustBe "foo"
      config.telegram.baseUri mustBe "bar"
    }
  }
}
