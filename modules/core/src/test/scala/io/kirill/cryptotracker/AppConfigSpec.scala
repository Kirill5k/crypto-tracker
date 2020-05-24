package io.kirill.cryptotracker

import io.kirill.cryptotracker.api.ApiClientSpec
import io.kirill.cryptotracker.config.AppConfig
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class AppConfigSpec extends AnyFreeSpec with Matchers {

  "An AppConfig" - {
    "should load itself without errors" in {
      val config = AppConfig.load

      config mustBe a[AppConfig]
    }
  }
}
