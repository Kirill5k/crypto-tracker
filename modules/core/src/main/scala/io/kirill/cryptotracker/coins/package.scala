package io.kirill.cryptotracker

import squants.Money

package object coins {

  final case class CoinSymbol(value: String) extends AnyVal
  final case class CoinName(value: String)   extends AnyVal

  sealed trait Cryptocoin {
    val symbol: CoinSymbol
    val name: CoinName
  }

  final case object Bitcoin extends Cryptocoin {
    val symbol = CoinSymbol("BTC")
    val name   = CoinName("Bitcoin")
  }

  final case object Ethereum extends Cryptocoin {
    val symbol = CoinSymbol("ETH")
    val name   = CoinName("Ethereum")
  }

  final case class CoinPrice(
      coin: Cryptocoin,
      currentPrice: Money,
      delta1h: BigDecimal,
      delta24h: BigDecimal,
      delta7d: BigDecimal,
      delta30d: BigDecimal
  )
}
