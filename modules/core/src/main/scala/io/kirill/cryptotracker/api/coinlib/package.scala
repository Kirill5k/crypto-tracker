package io.kirill.cryptotracker.api

package object coinlib {

  final case class ErrorResponse(
      error: String,
      remaining: Option[Int]
  )

  final case class GlobalResponse(
      coins: Int,
      markets: Int,
      total_market_cap: BigDecimal,
      total_volume_24h: BigDecimal,
      last_updated_timestamp: Long,
      remaining: Int
  )

  final case class CoinMarketExchange(
      name: String,
      volume_24h: BigDecimal,
      price: BigDecimal
  )

  final case class CoinMarket(
      symbol: String,
      volume_24h: BigDecimal,
      price: BigDecimal,
      exchanges: List[CoinMarketExchange]
  )

  final case class CoinResponse(
      symbol: String,
      show_symbol: String,
      name: String,
      rank: Int,
      price: BigDecimal,
      market_cap: BigDecimal,
      total_volume_24h: BigDecimal,
      low_24h: BigDecimal,
      high_24h: BigDecimal,
      delta_1h: BigDecimal,
      delta_24h: BigDecimal,
      delta_7d: BigDecimal,
      delta_30d: BigDecimal,
      markets: List[CoinMarket],
      last_updated_timestamp: Long,
      remaining: Int
  )
}
