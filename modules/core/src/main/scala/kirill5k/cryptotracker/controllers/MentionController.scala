package kirill5k.cryptotracker.controllers
import cats.effect.{ContextShift, Sync}
import io.chrisdavenport.log4cats.Logger
import kirill5k.cryptotracker.services.MentionService
import org.http4s.HttpRoutes

final private class MentionController[F[_]](
    private val service: MentionService[F]
) extends Controller[F] {

  override def routes(implicit F: Sync[F], logger: Logger[F], cs: ContextShift[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "mentions" :? DateFromQueryParam(from) +& DateToQueryParam(to) =>
        Ok(s"${from} and ${to}")
      case GET -> Root / "mentions" / TickerVar(ticker) :? OptionalDateFromQueryParam(from) +& OptionalDateToQueryParam(to) =>
        Ok(s"${ticker} ${from} and ${to}")
    }
}

object MentionController {
  def make[F[_]: Sync](service: MentionService[F]): F[Controller[F]] =
    Sync[F].delay(new MentionController[F](service))
}
