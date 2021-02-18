package kirill5k.cryptotracker.controllers

import java.time.{Instant, LocalDate, ZoneOffset}
import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.circe.generic.auto._
import kirill5k.cryptotracker.common.JsonCodecs
import kirill5k.cryptotracker.common.errors.AppError.MissingQueryParam
import kirill5k.cryptotracker.domain.Ticker
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, MessageFailure, ParseFailure, QueryParamDecoder, Response}

import scala.util.Try

final case class ErrorResponse(message: String)

trait Controller[F[_]] extends Http4sDsl[F] with JsonCodecs {

  implicit val instantQueryParamDecoder: QueryParamDecoder[Instant] =
    QueryParamDecoder[String].emap { dateString =>
      val date =
        if (dateString.length == 10) Try(LocalDate.parse(dateString)).map(_.atStartOfDay(ZoneOffset.UTC).toInstant)
        else Try(Instant.parse(dateString))
      date.toOption.toRight(ParseFailure(s"Invalid date format: $dateString", dateString))
    }

  object LimitQueryParam            extends OptionalQueryParamDecoderMatcher[Int]("limit")
  object OptionalDateFromQueryParam extends OptionalQueryParamDecoderMatcher[Instant]("from")
  object OptionalDateToQueryParam   extends OptionalQueryParamDecoderMatcher[Instant]("to")
  object DateFromQueryParam         extends QueryParamDecoderMatcher[Instant]("from")
  object DateToQueryParam           extends QueryParamDecoderMatcher[Instant]("to")

  object TickerVar {
    def unapply(str: String): Option[Ticker] =
      if (str.length > 1 && str.length < 5)
        Some(Ticker(str))
      else
        None
  }

  def routes(implicit F: Sync[F], logger: Logger[F], cs: ContextShift[F]): HttpRoutes[F]

  protected def withErrorHandling(
      response: => F[Response[F]]
  )(implicit
      F: Sync[F],
      logger: Logger[F]
  ): F[Response[F]] =
    response.handleErrorWith {
      case error: MissingQueryParam =>
        BadRequest(ErrorResponse(error.getMessage()))
      case error: MessageFailure =>
        logger.error(error)(s"error parsing json") *>
          BadRequest(ErrorResponse(error.getMessage()))
      case error =>
        logger.error(error)(s"unexpected error") *>
          InternalServerError(ErrorResponse(error.getMessage))
    }
}
