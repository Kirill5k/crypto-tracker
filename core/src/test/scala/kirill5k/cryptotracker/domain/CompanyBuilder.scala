package kirill5k.cryptotracker.domain

object CompanyBuilder {

  def company(
      ticker: Ticker,
      name: String = "Company",
      industry: String = "Industry",
      region: String = "Region"
  ): Company =
    Company(
      ticker,
      name,
      industry,
      region
    )
}
