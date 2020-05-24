package io.kirill.cryptotracker.coins

trait CoinService[F[_]] {
  def currentStats(coin: Cryptocoin): F[CoinStats]
}

object CoinService {}
