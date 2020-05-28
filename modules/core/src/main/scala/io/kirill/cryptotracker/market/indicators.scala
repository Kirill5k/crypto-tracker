package io.kirill.cryptotracker.market

import java.time.Instant

import io.kirill.cryptotracker.market.coins.Cryptocoin

object indicators {

  final case class Volume(
      mean: BigDecimal,
      current: BigDecimal
  )

  final case class SMA(
      sma20: BigDecimal,
      sma50: BigDecimal,
      sma200: BigDecimal
  )

  final case class EMA(
      ema8: BigDecimal,
      ema13: BigDecimal,
      ema21: BigDecimal,
      ema55: BigDecimal
  )

  final case class MarketIndicators(
      coin: Cryptocoin,
      timestamp: Instant,
      volume: Volume,
      sma: SMA,
      ema: EMA,
      rsi: BigDecimal,
      atr: BigDecimal
  )

  implicit final class MarketStatsOps(private val stats: MarketStats) extends AnyVal {

    private def EmaSmoothing: Double = 2.0
    private def RsiPeriod: Int       = 14
    private def AtrPeriod: Int       = 14

    def indicators: MarketIndicators =
      MarketIndicators(
        stats.coin,
        stats.end,
        Volume(av(), stats.priceBreakdown.last.volume),
        SMA(sma(20), sma(50), sma(200)),
        EMA(ema(8), ema(13), ema(21), ema(55)),
        rsi(),
        atr()
      )

    def atr(): BigDecimal = {
      val trs = stats.priceBreakdown.reverse
        .sliding(2)
        .map {
          case curr :: prev :: _ => curr.high.max(prev.close) - curr.low.min(prev.close)
        }
        .toList

      def calc(remainingTrs: List[BigDecimal]): BigDecimal =
        if (remainingTrs.size <= AtrPeriod) remainingTrs.sum / AtrPeriod
        else (calc(remainingTrs.tail) * (AtrPeriod - 1) + remainingTrs.head) / AtrPeriod
      calc(trs)
    }

    def ema(nPeriods: Int = stats.priceBreakdown.size): BigDecimal = {
      val prices = stats.priceBreakdown.map(_.close).reverse
      val k      = EmaSmoothing / (1 + nPeriods)
      def calc(prices: List[BigDecimal], current: Int = nPeriods): BigDecimal =
        if (current == 1) mean(prices)
        else prices.head * k + calc(prices.tail, current - 1) * (1 - k)
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
          val currGain           = if (remainingGains.head >= 0) remainingGains.head else BigDecimal(0)
          val currLoss           = if (remainingGains.head < 0) remainingGains.head.abs else BigDecimal(0)
          ((avgGain * (RsiPeriod - 1) + currGain) / RsiPeriod, (avgLoss * (RsiPeriod - 1) + currLoss) / RsiPeriod)
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
