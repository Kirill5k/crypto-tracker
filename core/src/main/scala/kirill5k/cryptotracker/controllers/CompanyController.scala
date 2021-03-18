package kirill5k.cryptotracker.controllers

import cats.effect.{ContextShift, Sync}
import cats.implicits._
import io.circe.generic.extras.auto._
import kirill5k.cryptotracker.services.CompanyService
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger

final private class CompanyController[F[_]](
    private val service: CompanyService[F]
) extends Controller[F] {

  override def routes(implicit F: Sync[F], logger: Logger[F], cs: ContextShift[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "companies" / TickerVar(ticker) =>
        withErrorHandling {
          service.find(ticker).flatMap(Ok(_))
        }
    }
}

object CompanyController {

  def make[F[_]: Sync](service: CompanyService[F]): F[Controller[F]] =
    Sync[F].delay(new CompanyController[F](service))
}
