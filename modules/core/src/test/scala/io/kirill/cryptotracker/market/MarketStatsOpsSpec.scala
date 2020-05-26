package io.kirill.cryptotracker.market

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class MarketStatsOpsSpec extends AnyFreeSpec with Matchers {

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
}
