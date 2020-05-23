package io.kirill.cryptotracker.clients.coinlib

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.circe
import io.circe.generic.auto._
import io.circe.parser._
import io.kirill.cryptotracker.coins.Cryptocurrency
import io.kirill.cryptotracker.config.AppConfig
import io.kirill.cryptotracker.errors.ApiClientError
import sttp.client._
import sttp.client.circe._
import sttp.model.MediaType

object CoinlibApi {
  def coin[F[_]: Sync: Logger](
      crypto: Cryptocurrency
  )(implicit ac: AppConfig, b: SttpBackend[F, Nothing, NothingT]): F[CoinResponse] =
    Logger[F].info(s"coinlib -> GET /coin?symbol=${crypto.symbol.value}") *>
      basicRequest
        .contentType(MediaType.ApplicationJson)
        .get(uri"${ac.coinlib.baseUri}/coin?key=${ac.coinlib.apiKey}&pref=GBP&symbol=${crypto.symbol.value}")
        .response(asJson[CoinResponse])
        .send()
        .flatMap(mapResponse[F, CoinResponse])

  def global[F[_]: Sync: Logger](implicit ac: AppConfig, b: SttpBackend[F, Nothing, NothingT]): F[GlobalResponse] =
    Logger[F].info(s"coinlib -> GET /global") *>
      basicRequest
        .contentType(MediaType.ApplicationJson)
        .get(uri"${ac.coinlib.baseUri}/global?key=${ac.coinlib.apiKey}&pref=GBP")
        .response(asJson[GlobalResponse])
        .send()
        .flatMap(mapResponse[F, GlobalResponse])

  private def mapResponse[F[_]: Sync: Logger, R](res: Response[Either[ResponseError[circe.Error], R]]): F[R] =
    res.body match {
      case Right(response) => response.pure[F]
      case Left(error) =>
        Sync[F].fromEither(decode[ErrorResponse](error.body).map(_.error).left.flatMap(_ => Right(error.body))).flatMap { e =>
          Logger[F].error(s"error sending request to coinlib: ${res.code} - $e") *>
            ApiClientError(res.code.code, e).raiseError[F, R]
        }
    }
}
