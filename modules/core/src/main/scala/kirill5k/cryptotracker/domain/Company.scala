package kirill5k.cryptotracker.domain

final case class Ticker(value: String)   extends AnyVal

final case class Company(
    ticker: Ticker,
    name: String,
    industry: String
)
