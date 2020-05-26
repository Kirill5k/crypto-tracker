package io.kirill.cryptotracker.coins

import java.time.Instant

import scala.concurrent.duration._
import scala.language.postfixOps

object Builder {

  def marketStats(coin: Cryptocoin = Bitcoin, timestamp: Instant = Instant.parse("2020-05-01T00:00:00Z")) =
    MarketStats(
      coin,
      Instant.parse("2020-05-01T00:00:00Z"),
      timestamp,
      1 day,
      List(
        OHLC(BigDecimal("8624.95905748"), BigDecimal("9056.66666973"), BigDecimal("8617.9557"), BigDecimal("8822.74810835"), BigDecimal("29048301.272027")),
        OHLC(BigDecimal("8823"), BigDecimal("9010"), BigDecimal("8758.4741002"), BigDecimal("8971.70977715"), BigDecimal("17229914.054838")),
        OHLC(BigDecimal("8971.70977715"), BigDecimal("9192.3076923"), BigDecimal("8723.00000001"), BigDecimal("8891.7452358"), BigDecimal("33921607.401117")),
        OHLC(BigDecimal("8894.67698991"), BigDecimal("8950"), BigDecimal("8525"), BigDecimal("8870.71256814"), BigDecimal("28923152.125324")),
        OHLC(BigDecimal("8870.71256815"), BigDecimal("9100"), BigDecimal("8769.1214"), BigDecimal("9021.23420893"), BigDecimal("21837237.738099"))
      )
    )
}
