package kirill5k.cryptotracker.common

import cats.effect.{Blocker, ContextShift, Sync}
import kirill5k.cryptotracker.domain.Subreddit
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

import scala.concurrent.duration.FiniteDuration

object config {

  final case class ServerConfig(
      host: String,
      port: Int
  )

  final case class MongoConfig(
      connectionUri: String
  )

  final case class TelegramConfig(
      baseUri: String,
      botKey: String,
      channelId: String
  )

  final case class AlphaVantageConfig(
      baseUri: String,
      apiKey: String
  )

  final case class RedditConfig(
      pushshiftUri: String,
      gummysearchUri: String,
      maxRetries: Int,
      searchPeriod: FiniteDuration,
      subreddits: List[Subreddit]
  )

  final case class AppConfig(
      server: ServerConfig,
      mongo: MongoConfig,
      reddit: RedditConfig,
      alphaVantage: AlphaVantageConfig,
      telegram: TelegramConfig
  )

  object AppConfig {

    def load[F[_]: Sync: ContextShift](blocker: Blocker): F[AppConfig] =
      ConfigSource.default.loadF[F, AppConfig](blocker)
  }

}
