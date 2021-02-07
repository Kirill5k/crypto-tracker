package kirill5k.cryptotracker.clients.telegram

import cats.effect.IO
import kirill5k.cryptotracker.RequestOps._
import kirill5k.cryptotracker.SttpClientSpec
import kirill5k.cryptotracker.common.config.TelegramConfig
import kirill5k.cryptotracker.common.errors.AppError
import sttp.client3.{Response, SttpBackend}
import sttp.model.StatusCode

class TelegramClientSpec extends SttpClientSpec {

  val message = Message("lorem ipsum dolor sit amet")
  val config = TelegramConfig("http://telegram.com", "BOT-KEY", "m1")

  "TelegramClient" should {

    "send message to the main channel" in {
      val testingBackend: SttpBackend[IO, Any] = backendStub
        .whenRequestMatchesPartial {
          case r if r.isGet && r.isGoingTo("telegram.com/botBOT-KEY/sendMessage") && r.hasParams(Map("chat_id" -> "m1", "text" -> message.text)) =>
            Response.ok("success")
          case _ => throw new RuntimeException()
        }

      val telegramClient = TelegramClient.make(config, testingBackend)

      val result = telegramClient.flatMap(_.send(message))

      result.unsafeToFuture().map(_ must be(()))
    }

    "return error when not success" in {
      val testingBackend: SttpBackend[IO, Any] = backendStub
        .whenRequestMatchesPartial {
          case r if r.isGet && r.isGoingTo("telegram.com/botBOT-KEY/sendMessage") =>
            Response("fail", StatusCode.BadRequest)
          case _ => throw new RuntimeException()
        }

      val telegramClient = TelegramClient.make(config, testingBackend)

      val result = telegramClient.flatMap(_.send(message))

      result.attempt.unsafeToFuture().map(_ must be(Left(AppError.Http(400, "error sending message to telegram channel m1: 400"))))
    }
  }
}
