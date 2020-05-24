package io.kirill.cryptotracker.coins

import cats.effect.IO
import io.kirill.cryptotracker.api.ApiClientSpec
import squants.market.GBP
import sttp.client.Response
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.testing.SttpBackendStub

class CoinPriceClientSpec extends ApiClientSpec {

  "A CoinlibPriceClient" - {
    "should return current price of a coin" in {
      implicit val testingBackend: SttpBackendStub[IO, Nothing] = AsyncHttpClientCatsBackend
        .stub[IO]
        .whenAnyRequest
        .thenRespond(Response.ok(json("coinlib/coin-success.json")))

      val result = for {
        client <- CoinPriceClient.make[IO]
        price <- client.getCurrentPrice(Bitcoin)
      } yield price

      result.asserting(_ must be (CoinPrice(GBP(7539.5741319913), BigDecimal(-0.57), BigDecimal(0.79), BigDecimal(-3.1), BigDecimal(31.11))))
    }
  }
}
