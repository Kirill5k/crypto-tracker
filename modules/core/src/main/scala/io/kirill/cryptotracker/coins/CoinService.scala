package io.kirill.cryptotracker.coins

trait CoinService[F[_]] {
  def currentPrice(coin: Cryptocoin): F[CoinPrice]
}

object CoinService {}
