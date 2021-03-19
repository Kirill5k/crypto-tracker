package kirill5k.cryptotracker.clients.alphavantage

private[alphavantage] object responses {

  final case class SymbolMatch(
      `1. symbol`: String,
      `2. name`: String,
      `3. type`: String,
      `4. region`: String
  )

  final case class SymbolSearchResponse(bestMatches: List[SymbolMatch])

  final case class TimeSeriesData(
      `1. open`: BigDecimal,
      `2. high`: BigDecimal,
      `3. low`: BigDecimal,
      `4. close`: BigDecimal,
      `5. volume`: BigDecimal
  )

  final case class WeeklyTimeSeriesResponse(
      `Weekly Time Series`: Map[String, TimeSeriesData]
  )
}
