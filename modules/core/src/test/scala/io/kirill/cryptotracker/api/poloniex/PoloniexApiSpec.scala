package io.kirill.cryptotracker.api.poloniex

import java.time.Instant

import cats.effect.IO
import io.kirill.cryptotracker.api.ApiClientSpec
import io.kirill.cryptotracker.errors.ApiClientError
import sttp.client
import sttp.client.Response
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.testing.SttpBackendStub
import sttp.model.StatusCode

import scala.concurrent.duration._
import scala.language.postfixOps

class PoloniexApiSpec extends ApiClientSpec {

  "A PoloniexApi" - {
    "GET /public?command=returnChartData" - {
      val start = Instant.parse("2020-05-01T00:00:00Z")
      val end = Instant.parse("2020-05-05T00:00:00Z")

      "return chart data for a given currency pair" in {
        implicit val testingBackend: SttpBackendStub[IO, Nothing] = AsyncHttpClientCatsBackend
          .stub[IO]
          .whenRequestMatchesPartial {
            case r if isPoloniexReq(r, "public", Map("command" -> "returnChartData", "currencyPair" -> "BTC_XMR", "period" -> "14400", "start" -> "1588291200", "end" -> "1588636800")) =>
              Response.ok(json("poloniex/chart-data-success.json"))
            case _ => throw new RuntimeException()
          }

        val response = PoloniexApi.getChartData[IO]("BTC_XMR", 4 hours, start, end)

        response.asserting(_.size must be(25))
      }

      "return error on failure" in {
        implicit val testingBackend: SttpBackendStub[IO, Nothing] = AsyncHttpClientCatsBackend
          .stub[IO]
          .whenRequestMatchesPartial {
            case r if isPoloniexReq(r, "public", Map("command" -> "returnChartData", "currencyPair" -> "BTC_XMR", "period" -> "14400", "start" -> "1588291200", "end" -> "1588636800")) =>
              Response(json("poloniex/error.json"), StatusCode.Ok)
            case _ => throw new RuntimeException()
          }

        val response = PoloniexApi.getChartData[IO]("BTC_XMR", 4 hours, start, end)

        response.assertThrows[ApiClientError]
      }
    }
  }

  def isPoloniexReq(req: client.Request[_, _], path: String, params: Map[String, String] = Map.empty): Boolean =
    req.uri.host == "poloniex.com" &&
      req.uri.path == List(path) &&
      req.uri.params.toMap.toSet[(String, String)].subsetOf(params.toSet)
}
