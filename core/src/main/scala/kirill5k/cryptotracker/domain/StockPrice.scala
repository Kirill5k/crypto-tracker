package kirill5k.cryptotracker.domain

import java.time.Instant

sealed trait TimeSeries
object TimeSeries {
  case object Weekly extends TimeSeries
  case object Daily  extends TimeSeries
}

final case class OHLCV(
    open: BigDecimal,
    high: BigDecimal,
    low: BigDecimal,
    close: BigDecimal,
    volume: BigDecimal,
    time: Instant
)

final case class StockPrice(
    ticker: Ticker,
    timeSeries: TimeSeries,
    prices: List[OHLCV]
)
