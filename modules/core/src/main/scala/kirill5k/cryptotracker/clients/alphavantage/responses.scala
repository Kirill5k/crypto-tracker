package kirill5k.cryptotracker.clients.alphavantage

private[alphavantage] object responses {

  final case class SymbolMatch(
      `1. symbol`: String,
      `2. name`: String,
      `3. type`: String,
      `4. region`: String
  )

  final case class SymbolSearchResponse(bestMatches: List[SymbolMatch])
}
