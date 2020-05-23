package io.kirill.cryptotracker

import cats.effect.{ConcurrentEffect, ContextShift, Resource}
import io.kirill.cryptotracker.config.AppConfig
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.{NothingT, SttpBackend}

final case class Resources[F[_]](
    sttpBackend: SttpBackend[F, Nothing, NothingT]
)

object Resources {

  private def makeSttpBackend[F[_]: ConcurrentEffect: ContextShift](
      implicit ac: AppConfig
  ): Resource[F, SttpBackend[F, Nothing, NothingT]] =
    Resource.make(AsyncHttpClientCatsBackend[F]())(_.close())

  def make[F[_]: ConcurrentEffect: ContextShift](
      implicit ac: AppConfig
  ): Resource[F, Resources[F]] =
    makeSttpBackend[F].map(b => Resources(b))
}
