package io.kirill.cryptotracker.market

import java.time.Instant

import cats.effect.IO
import io.kirill.cryptotracker.api.ApiClientSpec
import io.kirill.cryptotracker.market.coin.Bitcoin
import sttp.client.Response
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.testing.SttpBackendStub
import sttp.model.Uri.QuerySegment

import scala.concurrent.duration._
import scala.language.postfixOps

class CoinMarketClientSpec extends ApiClientSpec {

  "A PoloniexClient" - {
    "should return live market stats for a coin" in {
      implicit val testingBackend: SttpBackendStub[IO, Nothing] = AsyncHttpClientCatsBackend
        .stub[IO]
        .whenRequestMatches(r => r.uri.querySegments.contains(QuerySegment.KeyValue("currencyPair", "USDT_BTC")))
        .thenRespond(Response.ok(json("poloniex/usdt-btc-chart-data-success.json")))

      val result = for {
        client <- CoinMarketClient.make[IO]
        price  <- client.liveStats(Bitcoin, 1 day, 4 days, Instant.parse("2020-05-05T00:00:00Z"))
      } yield price

      result.asserting(
        _ must be(
          MarketStats(
            Bitcoin,
            Instant.parse("2020-05-01T00:00:00Z"),
            Instant.parse("2020-05-05T00:00:00Z"),
            1 day,
            List(
              OHLC(BigDecimal("8624.95905748"), BigDecimal("9056.66666973"), BigDecimal("8617.9557"), BigDecimal("8822.74810835"), BigDecimal("29048301.272027")),
              OHLC(BigDecimal("8823"), BigDecimal("9010"), BigDecimal("8758.4741002"), BigDecimal("8971.70977715"), BigDecimal("17229914.054838")),
              OHLC(BigDecimal("8971.70977715"), BigDecimal("9192.3076923"), BigDecimal("8723.00000001"), BigDecimal("8891.7452358"), BigDecimal("33921607.401117")),
              OHLC(BigDecimal("8894.67698991"), BigDecimal("8950"), BigDecimal("8525"), BigDecimal("8870.71256814"), BigDecimal("28923152.125324")),
              OHLC(BigDecimal("8870.71256815"), BigDecimal("9100"), BigDecimal("8769.1214"), BigDecimal("9021.23420893"), BigDecimal("21837237.738099"))
            )
          )
        )
      )
    }
  }
}
