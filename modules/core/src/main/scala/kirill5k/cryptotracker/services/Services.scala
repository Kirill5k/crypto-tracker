package kirill5k.cryptotracker.services

import cats.effect.{Sync, Timer}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import kirill5k.cryptotracker.clients.Clients

final case class Services[F[_]](
    mention: MentionService[F]
)

object Services {
  def make[F[_]: Sync: Timer: Logger](clients: Clients[F]): F[Services[F]] =
    MentionService
      .make(clients.reddit)
      .map(m => Services[F](m))
}
