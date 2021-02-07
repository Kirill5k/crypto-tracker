package kirill5k.cryptotracker.common

import cats.effect.{Blocker, ContextShift, Sync}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

import scala.concurrent.duration.FiniteDuration

object config {

  final case class TelegramConfig(
      baseUri: String,
      botKey: String,
      channelId: String
  )

  final case class Subreddit(value: String) extends AnyVal

  final case class RedditConfig(
      baseUri: String,
      subreddits: List[Subreddit]
  )

  final case class AppConfig(
      reddit: RedditConfig,
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
