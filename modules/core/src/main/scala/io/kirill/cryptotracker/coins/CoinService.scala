package io.kirill.cryptotracker.coins

import java.time.Instant

import cats.effect.Sync
import cats.implicits._

trait CoinService[F[_]] {
  def currentStats(coin: Cryptocoin): F[MarketStats]
}

final private class LiveCoinService[F[_]: Sync](
    val marketClient: CoinMarketClient[F],
    val marketStatsCache: MarketStatsCache[F]
) extends CoinService[F] {

  override def currentStats(coin: Cryptocoin): F[MarketStats] =
    for {
      s <- marketClient.liveStats(coin)
      _ <- marketStatsCache.add(s)
    } yield s
}

object CoinService {
  def make[F[_]: Sync](
      priceClient: CoinMarketClient[F],
      marketStatsCache: MarketStatsCache[F]
  ): F[CoinService[F]] =
    Sync[F].delay(new LiveCoinService[F](priceClient, marketStatsCache))
}
