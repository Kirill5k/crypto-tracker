package io.kirill.cryptotracker.market

object coins {

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

}
