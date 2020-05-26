package io.kirill.cryptotracker

import java.time.Instant

import scala.concurrent.duration.FiniteDuration

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

  final case class OHLC(
      open: BigDecimal,
      high: BigDecimal,
      low: BigDecimal,
      close: BigDecimal,
      volume: BigDecimal
  )

  final case class MarketStats(
      coin: Cryptocoin,
      start: Instant,
      end: Instant,
      period: FiniteDuration,
      priceBreakdown: List[OHLC]
  )
}
