package io.kirill.cryptotracker.api.poloniex

import java.time.Instant

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.parser._
import io.chrisdavenport.log4cats.Logger
import io.kirill.cryptotracker.config.AppConfig
import io.kirill.cryptotracker.errors.ApiClientError
import sttp.client._
import sttp.client.circe._
import sttp.model.{MediaType, StatusCode}

import scala.concurrent.duration.FiniteDuration

object PoloniexApi {

  def getChartData[F[_]: Sync: Logger](
      currencyPair: String,
      period: FiniteDuration,
      start: Instant,
      end: Instant
  )(
      implicit ac: AppConfig,
      b: SttpBackend[F, Nothing, NothingT]
  ): F[List[PoloniexChartData]] =
    Logger[F].info(s"poloniex -> GET /public?command=returnChartData&currencyPair=$currencyPair") *>
      basicRequest
        .contentType(MediaType.ApplicationJson)
        .get(uri"${ac.poloniex.baseUri}?command=returnChartData&currencyPair=${currencyPair}&start=${start.getEpochSecond}&end=${end.getEpochSecond}&period=${period.toSeconds}")
        .response(asJson[List[PoloniexChartData]])
        .send()
        .flatMap { r =>
          r.body match {
            case Right(chartData) => chartData.pure[F]
            case Left(error) =>
              val errorMessage = decode[PoloniexErrorResponse](error.body).fold(_ => error.body, _.error)
              Logger[F].error(s"error getting chart data from poloniex: $errorMessage") *>
                ApiClientError(StatusCode.BadRequest.code, errorMessage).raiseError[F, List[PoloniexChartData]]
          }
        }
}
