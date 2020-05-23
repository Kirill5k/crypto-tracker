package io.kirill.cryptotracker.clients.coinlib

import cats.effect.IO
import io.kirill.cryptotracker.clients.ApiClientSpec
import io.kirill.cryptotracker.config.AppConfig
import io.kirill.cryptotracker.errors.ApiClientError
import sttp.client
import sttp.client.Response
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.testing.SttpBackendStub
import sttp.model.StatusCode

class CoinlibApiSpec extends ApiClientSpec {

  import AppConfig._

  "A CoinlibApi" - {
    "GET /global" - {
      "return global stats on success" in {
        implicit val testingBackend: SttpBackendStub[IO, Nothing] = AsyncHttpClientCatsBackend
          .stub[IO]
          .whenRequestMatchesPartial {
            case r if isCoinlibReq(r, "global", Map("key" -> "api-key", "pref" -> "GBP")) =>
              Response.ok(json("coinlib/global-success.json"))
            case _ => throw new RuntimeException()
          }

        val response = CoinlibApi.global[IO]

        response.asserting(_.coins must be(6172))
      }

      "return error on failure" in {
        implicit val testingBackend: SttpBackendStub[IO, Nothing] = AsyncHttpClientCatsBackend
          .stub[IO]
          .whenRequestMatchesPartial {
            case r if isCoinlibReq(r, "global", Map("key" -> "api-key", "pref" -> "GBP")) =>
              Response(json("coinlib/bad-request-error.json"), StatusCode.BadRequest)
            case _ => throw new RuntimeException()
          }

        val response = CoinlibApi.global[IO]

        response.assertThrows[ApiClientError]
      }
    }
  }

  def isCoinlibReq(req: client.Request[_, _], path: String, params: Map[String, String] = Map.empty): Boolean =
    req.uri.host == "coinlib.io" &&
      req.uri.path == List("api", "v1") :+ path &&
      req.uri.params.toMap.toSet[(String, String)].subsetOf(params.toSet)
}
