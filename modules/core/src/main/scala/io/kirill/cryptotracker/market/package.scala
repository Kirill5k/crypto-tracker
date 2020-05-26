package io.kirill.cryptotracker

import java.time.Instant

import io.kirill.cryptotracker.market.coin.Cryptocoin

import scala.concurrent.duration.FiniteDuration

package object market {

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
