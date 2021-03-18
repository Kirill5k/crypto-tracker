package kirill5k.cryptotracker.services

import cats.effect.{Sync, Timer}
import cats.implicits._
import org.typelevel.log4cats.Logger
import kirill5k.cryptotracker.clients.Clients
import kirill5k.cryptotracker.repositories.Repositories

final case class Services[F[_]](
    mention: MentionService[F],
    company: CompanyService[F]
)

object Services {
  def make[F[_]: Sync: Timer: Logger](clients: Clients[F], repositories: Repositories[F]): F[Services[F]] =
    (
      MentionService.make(clients.reddit, repositories.mention),
      CompanyService.make(repositories.company, clients.alphaVantage)
    )
      .mapN(Services[F])
}
