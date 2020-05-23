package io.kirill.cryptotracker.api.telegram

import cats.effect.IO
import io.kirill.cryptotracker.api.ApiClientSpec
import io.kirill.cryptotracker.errors.ApiClientError
import sttp.client
import sttp.client.Response
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.testing.SttpBackendStub
import sttp.model.StatusCode

class TelegramApiSpec extends ApiClientSpec {

  "A TelegramApi" - {
    "GET /sendMessage" - {
      "return unit on success" in {
        implicit val testingBackend: SttpBackendStub[IO, Nothing] = AsyncHttpClientCatsBackend
          .stub[IO]
          .whenRequestMatchesPartial {
            case r if isTelegramReq(r, List("botbot-key", "sendMessage"), Map("chat_id" -> "channel-id", "text" -> "text message")) =>
              Response.ok("success")
            case _ => throw new RuntimeException()
          }

        val result = TelegramApi.sendMessage[IO]("text message")

        result.asserting(_ must be(()))
      }

      "return error on failure" in {
        implicit val testingBackend: SttpBackendStub[IO, Nothing] = AsyncHttpClientCatsBackend
          .stub[IO]
          .whenRequestMatchesPartial {
            case r if isTelegramReq(r, List("botbot-key", "sendMessage"), Map("chat_id" -> "channel-id", "text" -> "text message")) =>
              Response("error", StatusCode.BadRequest)
            case _ => throw new RuntimeException()
          }

        val result = TelegramApi.sendMessage[IO]("text message")

        result.assertThrows[ApiClientError]
      }
    }
  }

  def isTelegramReq(req: client.Request[_, _], paths: List[String], params: Map[String, String] = Map.empty): Boolean =
    req.uri.host == "api.telegram.org" &&
      req.uri.path == paths &&
      req.uri.params.toMap.toSet[(String, String)].subsetOf(params.toSet)
}
