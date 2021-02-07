package kirill5k.cryptotracker.common

import cats.effect.{ConcurrentEffect, ContextShift, Resource}
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

final case class Resources[F[_]](
    sttpBackend: SttpBackend[F, Any]
)

object Resources {

  private def makeSttpBackend[F[_]: ConcurrentEffect: ContextShift]: Resource[F, SttpBackend[F, Any]] =
    Resource.make(AsyncHttpClientCatsBackend[F]())(_.close())

  def make[F[_]: ConcurrentEffect: ContextShift]: Resource[F, Resources[F]] =
    makeSttpBackend[F].map(b => Resources(b))
}
