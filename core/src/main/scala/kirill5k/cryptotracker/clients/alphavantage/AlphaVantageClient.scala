package kirill5k.cryptotracker.clients.alphavantage

import cats.effect.Sync
import cats.implicits._
import org.typelevel.log4cats.Logger
import io.circe.generic.auto._
import kirill5k.cryptotracker.clients.alphavantage.responses.{SymbolSearchResponse, WeeklyTimeSeriesResponse}
import kirill5k.cryptotracker.common.config.AlphaVantageConfig
import kirill5k.cryptotracker.common.errors.AppError
import kirill5k.cryptotracker.domain.{Company, OHLCV, StockPrice, Ticker, TimeSeries}
import sttp.client3._
import sttp.client3.circe.asJson

import java.time.Instant

trait AlphaVantageClient[F[_]] {
  def findCompany(ticker: Ticker): F[Option[Company]]
  def getWeeklyPrices(ticker: Ticker): F[StockPrice]
}

final private class LiveAlphaVantageClient[F[_]](
    private val config: AlphaVantageConfig,
    private val backend: SttpBackend[F, Any]
)(implicit
    F: Sync[F],
    logger: Logger[F]
) extends AlphaVantageClient[F] {

  override def findCompany(ticker: Ticker): F[Option[Company]] =
    basicRequest
      .get(uri"${config.baseUri}/query?function=SYMBOL_SEARCH&keywords=${ticker.value}&apikey=${config.apiKey}")
      .response(asJson[SymbolSearchResponse])
      .send(backend)
      .flatMap { r =>
        r.body match {
          case Right(response) =>
            response.bestMatches
              .find(_.`1. symbol` == ticker.value)
              .map { symbolMatch =>
                Company(
                  ticker,
                  symbolMatch.`2. name`,
                  symbolMatch.`3. type`,
                  symbolMatch.`4. region`
                )
              }
              .pure[F]
          case Left(DeserializationException(body, error)) =>
            logger.error(s"error parsing alpha-vantage json: ${error.getMessage}\n$body") *>
              none[Company].pure[F]
          case Left(error) =>
            logger.error(s"error finding company by symbol: ${r.code}\n$error") *>
              none[Company].pure[F]
        }
      }

  override def getWeeklyPrices(ticker: Ticker, timeSeries: TimeSeries): F[StockPrice] =
    basicRequest
      .get(uri"${config.baseUri}/query?function=TIME_SERIES_WEEKLY&symbol=${ticker.value}&apikey=${config.apiKey}")
      .response(asJson[WeeklyTimeSeriesResponse])
      .send(backend)
      .flatMap { r =>
        r.body match {
          case Right(response) =>
            val ohlcvs = response.`Weekly Time Series`.map { case (date, ohclv) =>
              OHLCV(
                ohclv.`1. open`,
                ohclv.`2. high`,
                ohclv.`3. low`,
                ohclv.`4. close`,
                ohclv.`5. volume`,
                Instant.parse(s"${date}T00:00:00Z")
              )
            }
            StockPrice(ticker, TimeSeries.Weekly, ohlcvs.toList).pure[F]
          case Left(error) =>
            F.raiseError(AppError.Http(r.code.code, s"error obtaining stock price from alpha-vantage: ${error.getMessage}"))
        }
      }
}

object AlphaVantageClient {
  def make[F[_]: Sync: Logger](config: AlphaVantageConfig, backend: SttpBackend[F, Any]): F[AlphaVantageClient[F]] =
    Sync[F].delay(new LiveAlphaVantageClient[F](config, backend))
}
