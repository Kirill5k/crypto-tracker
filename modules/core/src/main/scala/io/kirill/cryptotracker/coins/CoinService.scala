package io.kirill.cryptotracker.coins

import cats.effect.Sync
import cats.effect.concurrent.Ref
import io.chrisdavenport.log4cats.Logger

trait CoinService[F[_]] {
  def currentStats(coin: Cryptocoin): F[CoinStats]
}

final private class LiveCoinService[F[_]: Sync: Logger](
    val priceClient: CoinPriceClient[F],
    val coinHistory: Ref[F, Map[CoinSymbol, CoinStats]]
) extends CoinService[F] {

  override def currentStats(coin: Cryptocoin): F[CoinStats] = ???
}

object CoinService {
  def make[F[_]: Sync: Logger](priceClient: CoinPriceClient[F]): F[CoinService[F]] = ???
}
