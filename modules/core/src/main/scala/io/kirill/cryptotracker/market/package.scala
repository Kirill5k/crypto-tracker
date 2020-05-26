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

  implicit final class MarketStatsOps(private val stats: MarketStats) extends AnyVal {

    private def EmaSmoothing: Double = 2.0

    def ema(nPeriods: Int = stats.priceBreakdown.size): BigDecimal = {
      val prices = priceBreakdownSlice(nPeriods).map(_.close).reverse
      val k = EmaSmoothing / (1 + nPeriods)
      def calc(prices: List[BigDecimal]): BigDecimal =
        if (prices.size == 1) prices.head
        else prices.head * k + calc(prices.tail) * (1-k)
      calc(prices)
    }

    def sma(nPeriods: Int = stats.priceBreakdown.size): BigDecimal = {
      priceBreakdownSlice(nPeriods).map(_.close).sum / nPeriods
    }

    def av(nPeriods: Int = stats.priceBreakdown.size): BigDecimal = {
      priceBreakdownSlice(nPeriods).map(_.volume).sum / nPeriods
    }

    private def priceBreakdownSlice(nPeriods: Int): List[OHLC] =
      stats.priceBreakdown.slice(stats.priceBreakdown.size - nPeriods, stats.priceBreakdown.size)
  }
}
