package io.kirill.cryptotracker.clients.coinlib

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.circe
import io.circe.generic.auto._
import io.circe.parser._
import io.kirill.cryptotracker.config.AppConfig
import io.kirill.cryptotracker.errors.ApiClientError
import sttp.client._
import sttp.client.circe._
import sttp.model.MediaType

object CoinlibApi {

  def global[F[_]: Sync: Logger](implicit ac: AppConfig, b: SttpBackend[F, Nothing, NothingT]): F[GlobalResponse] =
    Logger[F].info(s"coinlib -> GET /global") *>
      basicRequest
        .contentType(MediaType.ApplicationJson)
        .get(uri"${ac.coinlib.baseUri}/global")
        .response(asJson[GlobalResponse])
        .send()
        .flatMap(mapResponse[F, GlobalResponse])

  private def mapResponse[F[_]: Sync: Logger, R](res: Response[Either[ResponseError[circe.Error], R]]): F[R] =
    res.body match {
      case Right(response) => response.pure[F]
      case Left(error) =>
        for {
          errorMessage <- Sync[F].fromEither(decode[ErrorResponse](error.body).map(_.error).left.flatMap(_ => Right(error.body)))
          _ <- Logger[F].error(s"error sending request to coinlib: ${res.code} - $errorMessage")
          _ <- ApiClientError(res.code.code, errorMessage).raiseError[F, R]
        } yield ()
    }
}
