package io.kirill.cryptotracker.api.telegram

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.kirill.cryptotracker.config.AppConfig
import io.kirill.cryptotracker.errors.ApiClientError
import sttp.client._

object TelegramApi {

  def sendMessage[F[_]: Sync: Logger](
      message: String
  )(implicit ac: AppConfig, b: SttpBackend[F, Nothing, NothingT]): F[Unit] =
    Logger[F].info(s"telegram -> GET /sendMessage?text=$message") *>
      basicRequest
        .get(uri"${ac.telegram.baseUri}/bot${ac.telegram.botKey}/sendMessage?chat_id=${ac.telegram.channelId}&text=$message")
        .send()
        .flatMap { r =>
          r.body match {
            case Right(_) => Sync[F].unit
            case Left(error) =>
              Logger[F].error(s"error sending message to telegram: ${r.code} - $error") *>
                ApiClientError(r.code.code, s"error sending message to telegram: ${r.code} - ${error}").raiseError[F, Unit]
          }
        }
}
