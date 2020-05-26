package io.kirill.cryptotracker.api

package object poloniex {

  final case class PoloniexErrorResponse(error: String)

  final case class PoloniexChartData(
      date: Long,
      high: BigDecimal,
      low: BigDecimal,
      open: BigDecimal,
      close: BigDecimal,
      volume: BigDecimal,
      quoteVolume: BigDecimal,
      weightedAverage: BigDecimal
  )
}
