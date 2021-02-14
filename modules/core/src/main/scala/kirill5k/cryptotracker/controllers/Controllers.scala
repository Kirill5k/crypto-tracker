package kirill5k.cryptotracker.controllers

import cats.effect.{ContextShift, Sync}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import org.http4s.HttpRoutes
import org.http4s.server.Router

final case class Controllers[F[_]: Sync: Logger: ContextShift](
    health: Controller[F]
) {
  def routes: HttpRoutes[F] =
    Router(
      "" -> health.routes
    )
}

object Controllers {

  def make[F[_]: Sync: Logger: ContextShift]: F[Controllers[F]] =
    HealthController.make[F].map(hc => new Controllers[F](hc))
}
