package io.kirill.cryptotracker

import pureconfig._
import pureconfig.generic.auto._

object config {

  final case class CoinlibConfig(
      baseUri: String,
      apiKey: String
  )

  final case class TelegramConfig(
      baseUri: String,
      botKey: String,
      channelId: String
  )

  final case class AppConfig(
      coinlib: CoinlibConfig,
      telegram: TelegramConfig
  )

  object AppConfig {
    implicit val appConfig = ConfigSource.default.loadOrThrow[AppConfig]
  }
}
