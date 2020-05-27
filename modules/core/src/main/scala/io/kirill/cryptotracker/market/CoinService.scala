package io.kirill.cryptotracker.market

import cats.effect.Sync
import cats.implicits._
import io.kirill.cryptotracker.market.coins.Cryptocoin

import scala.concurrent.duration._
import scala.language.postfixOps

trait CoinService[F[_]] {
  def currentStats(coin: Cryptocoin): F[MarketStats]
}

final private class LiveCoinService[F[_]: Sync](
    val marketClient: CoinMarketClient[F],
    val marketStatsCache: MarketStatsCache[F]
) extends CoinService[F] {

  override def currentStats(coin: Cryptocoin): F[MarketStats] =
    for {
      s <- marketClient.liveStats(coin, 4 hours, 200 days)
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
