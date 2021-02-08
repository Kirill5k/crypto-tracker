package kirill5k.cryptotracker.domain

final case class Ticker(value: String)   extends AnyVal
final case class Name(value: String)     extends AnyVal
final case class Industry(value: String) extends AnyVal

final case class Company(
    ticker: Ticker,
    name: Name,
    industry: Industry
)
