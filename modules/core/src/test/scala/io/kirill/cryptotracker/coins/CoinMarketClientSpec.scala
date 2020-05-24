package io.kirill.cryptotracker.coins

import java.time.Instant

import cats.effect.IO
import io.kirill.cryptotracker.api.ApiClientSpec
import squants.market.GBP
import sttp.client.Response
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.testing.SttpBackendStub

class CoinMarketClientSpec extends ApiClientSpec {

  "A CoinlibPriceClient" - {
    "should return live market stats for a coin" in {
      implicit val testingBackend: SttpBackendStub[IO, Nothing] = AsyncHttpClientCatsBackend
        .stub[IO]
        .whenAnyRequest
        .thenRespond(Response.ok(json("coinlib/coin-success.json")))

      val result = for {
        client <- CoinMarketClient.make[IO]
        price  <- client.liveStats(Bitcoin)
      } yield price

      result.asserting(
        _ must be(
          MarketStats(
            Bitcoin,
            CoinPrice(GBP(7539.5741319913), BigDecimal(-0.57), BigDecimal(0.79), BigDecimal(-3.1), BigDecimal(31.11)),
            MarketVolume(BigDecimal("4686477447.1065225601")),
            Instant.parse("2020-05-23T10:03:14Z")
          )
        )
      )
    }
  }
}
