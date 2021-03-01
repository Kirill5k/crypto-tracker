package kirill5k.cryptotracker.controllers

import cats.effect.{ContextShift, Sync}
import cats.implicits._
import org.typelevel.log4cats.Logger
import kirill5k.cryptotracker.services.Services
import org.http4s.HttpRoutes
import org.http4s.server.Router

final case class Controllers[F[_]: Sync: Logger: ContextShift](
    health: Controller[F],
    mention: Controller[F]
) {
  def routes: HttpRoutes[F] =
    Router(
      "" -> health.routes,
      "/api" -> mention.routes
    )
}

object Controllers {

  def make[F[_]: Sync: Logger: ContextShift](services: Services[F]): F[Controllers[F]] =
    (
      HealthController.make[F],
      MentionController.make[F](services.mention)
    ).mapN(Controllers[F])
}
