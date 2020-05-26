package io.kirill.cryptotracker.api.coinlib

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
  def coin[F[_]: Sync: Logger](
      coinSymbol: String
  )(implicit ac: AppConfig, b: SttpBackend[F, Nothing, NothingT]): F[CoinlibCoinResponse] =
    Logger[F].info(s"coinlib -> GET /coin?symbol=$coinSymbol") *>
      basicRequest
        .contentType(MediaType.ApplicationJson)
        .get(uri"${ac.coinlib.baseUri}/coin?key=${ac.coinlib.apiKey}&pref=GBP&symbol=${coinSymbol.toUpperCase}")
        .response(asJson[CoinlibCoinResponse])
        .send()
        .flatMap(mapResponse[F, CoinlibCoinResponse])

  def global[F[_]: Sync: Logger](implicit ac: AppConfig, b: SttpBackend[F, Nothing, NothingT]): F[CoinlibGlobalResponse] =
    Logger[F].info(s"coinlib -> GET /global") *>
      basicRequest
        .contentType(MediaType.ApplicationJson)
        .get(uri"${ac.coinlib.baseUri}/global?key=${ac.coinlib.apiKey}&pref=GBP")
        .response(asJson[CoinlibGlobalResponse])
        .send()
        .flatMap(mapResponse[F, CoinlibGlobalResponse])

  private def mapResponse[F[_]: Sync: Logger, R](res: Response[Either[ResponseError[circe.Error], R]]): F[R] =
    res.body match {
      case Right(response) => response.pure[F]
      case Left(error) =>
        Sync[F].fromEither(decode[CoinlibErrorResponse](error.body).map(_.error).left.flatMap(_ => Right(error.body))).flatMap { e =>
          Logger[F].error(s"error sending request to coinlib: ${res.code} - $e") *>
            ApiClientError(res.code.code, e).raiseError[F, R]
        }
    }
}
