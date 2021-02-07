package kirill5k.cryptotracker.common

import cats.effect.{Blocker, ContextShift, Sync}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

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

    def load[F[_]: Sync: ContextShift](blocker: Blocker): F[AppConfig] =
      ConfigSource.default.loadF[F, AppConfig](blocker)
  }

}
