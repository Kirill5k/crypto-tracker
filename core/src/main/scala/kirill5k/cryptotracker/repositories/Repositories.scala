package kirill5k.cryptotracker.repositories

import cats.effect.Concurrent
import cats.implicits._
import mongo4cats.client.MongoClientF

final case class Repositories[F[_]](
    mention: MentionRepository[F],
    company: CompanyRepository[F]
)

object Repositories {
  def make[F[_]: Concurrent](client: MongoClientF[F]): F[Repositories[F]] =
    (MentionRepository.make[F](client), CompanyRepository.make[F](client))
      .mapN(Repositories[F])
}
