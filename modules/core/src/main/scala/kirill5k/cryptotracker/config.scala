package kirill5k.cryptotracker

import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration

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

  final case class CacheConfig(
      expiresIn: FiniteDuration
  )

  object AppConfig {
    def load: AppConfig = ConfigSource.default.loadOrThrow[AppConfig]
  }
}
