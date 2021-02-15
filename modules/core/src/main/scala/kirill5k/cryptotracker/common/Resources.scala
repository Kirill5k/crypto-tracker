package kirill5k.cryptotracker.common

import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, Resource}
import cats.implicits._
import kirill5k.cryptotracker.common.config.{AppConfig, MongoConfig}
import mongo4cats.client.MongoClientF
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

final case class Resources[F[_]](
    sttpBackend: SttpBackend[F, Any],
    mongoClient: MongoClientF[F]
)

object Resources {

  private def mongoClient[F[_]: Concurrent](config: MongoConfig): Resource[F, MongoClientF[F]] =
    MongoClientF.fromConnectionString[F](config.connectionUri)

  private def sttpBackend[F[_]: ConcurrentEffect: ContextShift]: Resource[F, SttpBackend[F, Any]] =
    Resource.make(AsyncHttpClientCatsBackend[F]())(_.close())

  def make[F[_]: ConcurrentEffect: ContextShift](config: AppConfig): Resource[F, Resources[F]] =
    (
      sttpBackend[F],
      mongoClient[F](config.mongo)
    ).mapN(Resources[F])
}
