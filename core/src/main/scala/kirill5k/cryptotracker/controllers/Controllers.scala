package kirill5k.cryptotracker.controllers

import cats.effect.Sync
import cats.implicits._
import kirill5k.cryptotracker.services.Services
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

final case class Controllers[F[_]: Sync: Logger](
    health: Controller[F],
    mention: Controller[F],
    company: Controller[F]
) {
  def routes: HttpRoutes[F] =
    Router(
      "" -> health.routes,
      "/api" -> mention.routes,
      "/api" -> company.routes,
    )
}

object Controllers {

  def make[F[_]: Sync: Logger](services: Services[F]): F[Controllers[F]] =
    (
      HealthController.make[F],
      MentionController.make[F](services.mention),
      CompanyController.make[F](services.company)
    ).mapN(Controllers[F])
}
