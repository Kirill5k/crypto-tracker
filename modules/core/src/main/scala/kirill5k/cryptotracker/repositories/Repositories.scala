package kirill5k.cryptotracker.repositories

import cats.effect.Concurrent
import cats.implicits._
import mongo4cats.client.MongoClientF

final case class Repositories[F[_]](
    mention: MentionRepository[F]
)

object Repositories {
  def make[F[_]: Concurrent](client: MongoClientF[F]): F[MentionRepository[F]] =
    MentionRepository
      .make[F](client)
      .map(Repositories[F])
}
