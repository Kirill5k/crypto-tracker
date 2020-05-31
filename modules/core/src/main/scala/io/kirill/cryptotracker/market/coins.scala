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

  final case object Litecoin extends Cryptocoin {
    override val symbol: CoinSymbol = CoinSymbol("LTC")
    override val name: CoinName = CoinName("Litecoin")
  }

  final case object Ethereumclassic extends Cryptocoin {
    override val symbol: CoinSymbol = CoinSymbol("ETC")
    override val name: CoinName = CoinName("Ethereumclassic")
  }

  final case object Bitcoincash extends Cryptocoin {
    override val symbol: CoinSymbol = CoinSymbol("BCH")
    override val name: CoinName = CoinName("Bitcoincash")
  }

  final case object Ripple extends Cryptocoin {
    override val symbol: CoinSymbol = CoinSymbol("XRP")
    override val name: CoinName = CoinName("Ripple")
  }

  final case object Neo extends Cryptocoin {
    override val symbol: CoinSymbol = CoinSymbol("NEO")
    override val name: CoinName = CoinName("Neo")
  }
}
