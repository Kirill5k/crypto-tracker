package io.kirill.cryptotracker.coins

import java.time.Instant

import squants.market.{GBP, Money}

object Builder {

  def coinPrice(
      price: Money = GBP(7500),
      d1h: BigDecimal = BigDecimal(0),
      d24h: BigDecimal = BigDecimal(0)
  ): CoinPrice =
    CoinPrice(price, d1h, d24h, BigDecimal(0), BigDecimal(0))

  def marketStats(coin: Cryptocoin = Bitcoin, timestamp: Instant = Instant.now, price: Money = GBP(7500)) =
    MarketStats(
      coin,
      coinPrice(price),
      MarketVolume(BigDecimal(1000000)),
      timestamp
    )
}
