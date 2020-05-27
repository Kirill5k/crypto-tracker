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
    private def RsiPeriod: Int = 14

    def ema(nPeriods: Int = stats.priceBreakdown.size): BigDecimal = {
      val prices = stats.priceBreakdown.map(_.close).reverse
      val k = EmaSmoothing / (1 + nPeriods)
      def calc(prices: List[BigDecimal], current: Int = nPeriods): BigDecimal =
        if (current == 1) mean(prices)
        else prices.head * k + calc(prices.tail, current - 1) * (1-k)
      calc(prices)
    }

    def sma(nPeriods: Int = stats.priceBreakdown.size): BigDecimal =
      mean(priceBreakdownSlice(nPeriods).map(_.close))

    def av(nPeriods: Int = stats.priceBreakdown.size): BigDecimal =
      mean(priceBreakdownSlice(nPeriods).map(_.volume))

    def rsi(): BigDecimal = {
      val gains = stats.priceBreakdown.map(_.close).sliding(2).map(pair => pair.tail.head - pair.head).toList.reverse

      def calc(remainingGains: List[BigDecimal]): (BigDecimal, BigDecimal) =
        if (remainingGains.size <= RsiPeriod) {
          val (ups, downs) = gains.tail.partition(_ > 0)
          (ups.sum / RsiPeriod, downs.sum.abs / RsiPeriod)
        } else {
          val (avgGain, avgLoss) = calc(remainingGains.tail)
          val currGain = if (remainingGains.head >= 0) remainingGains.head else BigDecimal(0)
          val currLoss = if (remainingGains.head < 0) remainingGains.head.abs else BigDecimal(0)
          ((avgGain * (RsiPeriod-1) + currGain) / RsiPeriod, (avgLoss * (RsiPeriod-1) + currLoss) / RsiPeriod)
        }

      val (avgGain, avgLoss) = calc(gains)

      val rs = avgGain / avgLoss
      (100.0 - (100.0 / (1 + rs)))
    }

    private def mean(ns: List[BigDecimal]): BigDecimal =
      ns.sum / ns.size

    private def priceBreakdownSlice(nPeriods: Int): List[OHLC] =
      stats.priceBreakdown.slice(stats.priceBreakdown.size - nPeriods, stats.priceBreakdown.size)
  }
}
