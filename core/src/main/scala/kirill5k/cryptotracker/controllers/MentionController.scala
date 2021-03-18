package kirill5k.cryptotracker.controllers

import cats.effect.{ContextShift, Sync}
import cats.implicits._
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration
import org.typelevel.log4cats.Logger
import kirill5k.cryptotracker.common.errors.AppError.MissingQueryParam
import kirill5k.cryptotracker.controllers.MentionController.{DateRange, MentionSummaries}
import kirill5k.cryptotracker.domain.{Mention, Ticker}
import kirill5k.cryptotracker.services.MentionService
import org.http4s.HttpRoutes

import java.time.Instant

final private class MentionController[F[_]](
    private val service: MentionService[F]
) extends Controller[F] {

  override def routes(implicit F: Sync[F], logger: Logger[F], cs: ContextShift[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "mentions" :? OptionalDateFromQueryParam(from) +& OptionalDateToQueryParam(to) =>
        withErrorHandling {
          for {
            dateRange <- fromAndTo(from, to)
            mentions  <- service.findAll(dateRange.from, dateRange.to)
            res       <- Ok(mentions)
          } yield res
        }
      case GET -> Root / "mentions" / "summary" :? OptionalDateFromQueryParam(from) +& OptionalDateToQueryParam(to) =>
        withErrorHandling {
          for {
            dateRange <- fromAndTo(from, to)
            mentions  <- service.findAll(dateRange.from, dateRange.to)
            res       <- Ok(MentionSummaries.from(mentions, dateRange))
          } yield res
        }
      case GET -> Root / "mentions" / TickerVar(ticker) :? OptionalDateFromQueryParam(from) +& OptionalDateToQueryParam(to) =>
        withErrorHandling {
          service.findBy(ticker, from, to).flatMap(Ok(_))
        }
    }

  private def fromAndTo(from: Option[Instant], to: Option[Instant])(implicit F: Sync[F]): F[DateRange] =
    (F.fromOption(from, MissingQueryParam("from")), F.fromOption(to, MissingQueryParam("to"))).mapN(DateRange.apply)
}

object MentionController {

  final case class DateRange(
      from: Instant,
      to: Instant
  )

  final case class MentionSummary(
      ticker: Ticker,
      total: Int,
      times: List[Instant]
  )

  final case class MentionSummaries(
      dateRange: DateRange,
      summaries: List[MentionSummary]
  )

  object MentionSummaries {
    def from(mentions: List[Mention], dateRange: DateRange): MentionSummaries = {
      val summaries = mentions
        .groupBy(_.ticker)
        .map { case (t, ms) => MentionSummary(t, ms.size, ms.map(_.time)) }
        .toList
        .sorted(Ordering.by((_: MentionSummary).total).reverse)
      MentionSummaries(dateRange, summaries)
    }
  }

  def make[F[_]: Sync](service: MentionService[F]): F[Controller[F]] =
    Sync[F].delay(new MentionController[F](service))
}
