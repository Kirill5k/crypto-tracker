package io.kirill.cryptotracker.coins

import java.time.Instant

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.kirill.cryptotracker.api.poloniex.PoloniexApi
import io.kirill.cryptotracker.config.AppConfig
import sttp.client.{NothingT, SttpBackend}

import scala.concurrent.duration.FiniteDuration

sealed trait CoinMarketClient[F[_]] {
  def liveStats(
      coin: Cryptocoin,
      period: FiniteDuration,
      duration: FiniteDuration,
      end: Instant = Instant.now()
  ): F[MarketStats]
}

final private class PoloniexClient[F[_]: Sync: Logger](
    implicit val backend: SttpBackend[F, Nothing, NothingT],
    val config: AppConfig
) extends CoinMarketClient[F] {

  override def liveStats(
      coin: Cryptocoin,
      period: FiniteDuration,
      duration: FiniteDuration,
      end: Instant = Instant.now()
  ): F[MarketStats] = {
    val pair  = s"USDT_${coin.symbol.value}"
    val start = end.minusNanos(duration.toNanos)
    PoloniexApi.getChartData[F](pair, period, start, end).map { cd =>
      MarketStats(
        coin,
        start,
        end,
        period,
        cd.map(d => OHLC(d.open, d.high, d.low, d.close, d.volume))
      )
    }
  }
}

object CoinMarketClient {
  def make[F[_]: Sync: Logger](
      implicit backend: SttpBackend[F, Nothing, NothingT],
      config: AppConfig
  ): F[CoinMarketClient[F]] =
    Sync[F].delay(new PoloniexClient[F])
}
