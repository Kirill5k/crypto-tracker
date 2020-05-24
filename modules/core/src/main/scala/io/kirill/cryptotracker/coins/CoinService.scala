package io.kirill.cryptotracker.coins

import java.time.Instant

import cats.effect.Sync
import cats.implicits._

trait CoinService[F[_]] {
  def currentStats(coin: Cryptocoin): F[CoinStats]
}

final private class LiveCoinService[F[_]: Sync](
    val priceClient: CoinPriceClient[F],
    val coinStatsCache: CoinStatsCache[F]
) extends CoinService[F] {

  override def currentStats(coin: Cryptocoin): F[CoinStats] =
    for {
      p <- priceClient.getCurrentPrice(coin)
      s = CoinStats(coin, p, Instant.now)
      _ <- coinStatsCache.add(s)
    } yield s
}

object CoinService {
  def make[F[_]: Sync](priceClient: CoinPriceClient[F], coinStatsCache: CoinStatsCache[F]): F[CoinService[F]] =
    Sync[F].delay(new LiveCoinService[F](priceClient, coinStatsCache))
}
