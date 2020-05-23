package io.kirill.cryptotracker

object coins {

  final case class CryptoCurrencySymbol(value: String) extends AnyVal
  final case class CryptoCurrencyName(value: String) extends AnyVal

  sealed trait Cryptocurrency {
    val symbol: CryptoCurrencySymbol
    val name: CryptoCurrencyName
  }

  final case object Bitcoin extends Cryptocurrency {
    val symbol = CryptoCurrencySymbol("BTC")
    val name = CryptoCurrencyName("Bitcoin")
  }

  final case object Ethereum extends Cryptocurrency {
    val symbol = CryptoCurrencySymbol("ETH")
    val name = CryptoCurrencyName("Ethereum")
  }
}
