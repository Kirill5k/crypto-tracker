package kirill5k.cryptotracker.clients.telegram

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import kirill5k.cryptotracker.common.config.TelegramConfig
import kirill5k.cryptotracker.common.errors.AppError
import sttp.client3._

final case class Message(
    text: String
)

trait TelegramClient[F[_]] {
  def send(message: Message): F[Unit]
}

final private class TelegramApiClient[F[_]: Sync](
    private val config: TelegramConfig,
    private val backend: SttpBackend[F, Any]
)(implicit val L: Logger[F])
    extends TelegramClient[F] {

  def send(message: Message): F[Unit] =
    basicRequest
      .get(uri"${config.baseUri}/bot${config.botKey}/sendMessage?chat_id=${config.channelId}&text=${message.text}")
      .send(backend)
      .flatMap { r =>
        r.body match {
          case Right(_) => ().pure[F]
          case Left(error) =>
            L.error(s"error sending message to telegram: ${r.code}\n$error") *>
              AppError.Http(r.code.code, s"error sending message to telegram channel ${config.channelId}: ${r.code}").raiseError[F, Unit]
        }
      }
}

object TelegramClient {
  def make[F[_]: Sync: Logger](
      config: TelegramConfig,
      backend: SttpBackend[F, Any]
  ): F[TelegramClient[F]] =
    Sync[F].delay(new TelegramApiClient[F](config, backend))
}
