package kirill5k.cryptotracker.clients

import cats.Parallel
import cats.effect.{Sync, Timer}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import kirill5k.cryptotracker.clients.reddit.RedditClient
import kirill5k.cryptotracker.clients.telegram.TelegramClient
import kirill5k.cryptotracker.common.config.AppConfig
import sttp.client3.SttpBackend

final case class Clients[F[_]](
    reddit: RedditClient[F],
    telegram: TelegramClient[F]
)

object Clients {
  def make[F[_]: Parallel: Sync: Timer: Logger](config: AppConfig, backend: SttpBackend[F, Any]): F[Clients[F]] =
    (
      RedditClient.make[F](config.reddit, backend),
      TelegramClient.make[F](config.telegram, backend)
    ).parMapN((r, t) => new Clients[F](r, t))
}
