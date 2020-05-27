package io.kirill.cryptotracker.market

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class MarketStatsOpsSpec extends AnyFreeSpec with Matchers {

  "atr" - {
    val marketStats = Builder.marketStats()

    "calculate average true range" in {
      val stats = marketStats.copy(
        priceBreakdown = List(
          OHLC(BigDecimal(0), BigDecimal(48.70), BigDecimal(47.79), BigDecimal(48.16), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(48.72), BigDecimal(48.14), BigDecimal(48.61), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(48.90), BigDecimal(48.39), BigDecimal(48.75), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(48.87), BigDecimal(48.37), BigDecimal(48.63), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(48.82), BigDecimal(48.24), BigDecimal(48.74), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(49.05), BigDecimal(48.64), BigDecimal(49.03), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(49.20), BigDecimal(48.94), BigDecimal(49.07), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(49.35), BigDecimal(48.86), BigDecimal(49.32), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(49.92), BigDecimal(49.50), BigDecimal(49.91), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(50.19), BigDecimal(49.87), BigDecimal(50.13), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(50.12), BigDecimal(49.20), BigDecimal(49.53), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(49.66), BigDecimal(48.90), BigDecimal(49.50), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(49.88), BigDecimal(49.43), BigDecimal(49.75), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(50.19), BigDecimal(49.73), BigDecimal(50.03), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(50.36), BigDecimal(49.26), BigDecimal(50.31), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(50.57), BigDecimal(50.09), BigDecimal(50.52), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(50.65), BigDecimal(50.30), BigDecimal(50.41), BigDecimal(0)),
        )
      )

      val atr = stats.atr()

      atr must be(BigDecimal("0.5464686588921282798833819241982508"))
    }
  }

  "sma" - {
    val marketStats = Builder.marketStats()

    "calculate sma for a given period" in {
      val stats = marketStats.copy(
        priceBreakdown = List(
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(1), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(2), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(3), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(4), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(5), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(6), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(7), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(8), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(9), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(10), BigDecimal(0))
        )
      )

      val sma = stats.sma(5)

      sma must be(8)
    }
  }

  "av" - {
    val marketStats = Builder.marketStats()

    "calculate average volume for a given period" in {
      val stats = marketStats.copy(
        priceBreakdown = List(
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(1)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(2)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(3)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(4)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(5)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(6)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(7)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(8)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(9)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(10))
        )
      )

      val sma = stats.av(5)

      sma must be(8)
    }
  }

  "ema" - {
    val marketStats = Builder.marketStats()

    "calculate exponential moving average for a given period for another set" in {
      val stats = marketStats.copy(
        priceBreakdown = List(
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(64.75), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(63.79), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(63.73), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(63.73), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(63.55), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(63.19), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(63.91), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(63.85), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(62.95), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(63.37), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(61.33), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(61.51), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(61.87), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(60.25), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(59.35), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(59.95), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(58.93), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(57.68), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(58.82), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(58.87), BigDecimal(0))
        )
      )

      val ema = stats.ema(10)

      ema must be(BigDecimal("59.90473053344797710690800684012607"))
    }
  }

  "rsi" - {
    val marketStats = Builder.marketStats()

    "calculate relative strength index" in {
      val stats = marketStats.copy(
        priceBreakdown = List(
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(44.34), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(44.09), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(44.15), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(43.61), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(44.33), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(44.83), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(45.10), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(45.42), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(45.84), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.08), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(45.89), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.03), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(45.61), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.28), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.28), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.00), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.03), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.41), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.22), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(45.64), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.21), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.25), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(45.71), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(46.45), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(45.78), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(45.35), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(44.03), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(44.18), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(44.22), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(44.57), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(43.42), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(42.66), BigDecimal(0)),
          OHLC(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(43.13), BigDecimal(0)),
        )
      )

      val rsi = stats.rsi()

      rsi must be(BigDecimal("35.39125407085985769098708982169985"))
    }
  }
}
