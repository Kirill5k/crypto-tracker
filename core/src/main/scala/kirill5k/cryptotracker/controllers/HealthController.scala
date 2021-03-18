package kirill5k.cryptotracker.controllers
import cats.effect.Sync
import io.circe.generic.auto._
import kirill5k.cryptotracker.controllers.HealthController.AppStatus
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger

class HealthController[F[_]] extends Controller[F] {

  override def routes(implicit F: Sync[F], logger: Logger[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "health" / "status" =>
        Ok(AppStatus(true))
    }
}

object HealthController {

  final case class AppStatus(status: Boolean)

  def make[F[_]: Sync]: F[Controller[F]] =
    Sync[F].delay(new HealthController[F])
}
