package io.kirill.cryptotracker.market

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class MarketStatsOpsSpec extends AnyFreeSpec with Matchers {

  import io.kirill.cryptotracker.market._

  "sma" - {
    val marketStats = Builder.marketStats()

    "calculate sma for a given period" in {
      val stats = marketStats.copy(priceBreakdown = List(
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
      ))

      val sma = stats.sma(5)

      sma must be (8)
    }
  }
}
