package kirill5k.cryptotracker.clients.reddit

import cats.NonEmptyParallel
import cats.effect.{Sync, Timer}
import cats.implicits._
import org.typelevel.log4cats.Logger
import io.circe.generic.auto._
import kirill5k.cryptotracker.clients.reddit.mappers.MentionsMapper
import kirill5k.cryptotracker.clients.reddit.responses.{GummysearchSubmissionsResponse, PushshiftSubmissionsResponse}
import kirill5k.cryptotracker.common.config.RedditConfig
import kirill5k.cryptotracker.common.json._
import kirill5k.cryptotracker.common.errors.AppError
import kirill5k.cryptotracker.domain.{Mention, Subreddit}
import sttp.client3._
import sttp.client3.circe.asJson

import scala.concurrent.duration._

trait RedditClient[F[_]] {
  def findMentions(subreddit: Subreddit, duration: FiniteDuration): F[List[Mention]]
}

final private class LiveRedditClient[F[_]: Sync: NonEmptyParallel](
    private val config: RedditConfig,
    private val backend: SttpBackend[F, Any]
)(implicit
    logger: Logger[F],
    timer: Timer[F]
) extends RedditClient[F] {

  override def findMentions(subreddit: Subreddit, duration: FiniteDuration): F[List[Mention]] =
    (queryPushshift(subreddit, duration, config.maxRetries), queryGummysearch(subreddit, config.maxRetries))
      .parMapN(_ ::: _)
      .map(_.distinctBy(m => (m.ticker, m.url)))

  private def queryPushshift(subreddit: Subreddit, duration: FiniteDuration, attempt: Int): F[List[Mention]] =
    timer.clock.realTime(SECONDS).flatMap { epocSeconds =>
      val dur = epocSeconds - duration.toSeconds
      basicRequest
        .get(uri"${config.pushshiftUri}/reddit/submission/search?subreddit=${subreddit.value}&after=$dur")
        .response(asJson[PushshiftSubmissionsResponse])
        .send(backend)
        .flatMap { r =>
          r.body match {
            case Right(response) =>
              response.data.flatMap(MentionsMapper.map).pure[F]
            case Left(DeserializationException(body, error)) =>
              logger.error(s"error parsing pushshift json: ${error.getMessage}\n$body") *>
                AppError.Json(s"error parsing pushshift json: ${error.getMessage}").raiseError[F, List[Mention]]
            case Left(_) if attempt == 0 =>
              logger.error(s"exceeded maximum amount of attempts getting submissions from pushshift") *>
                List.empty[Mention].pure[F]
            case Left(error) =>
              logger.error(s"error getting submissions from pushshift: ${r.code}\n$error") *>
                timer.sleep(2.second) *> queryPushshift(subreddit, duration + 5.second, attempt - 1)
          }
        }
    }

  private def queryGummysearch(subreddit: Subreddit, attempt: Int): F[List[Mention]] =
    basicRequest
      .get(uri"${config.gummysearchUri}/api/v1/reddit/submissions/?type=submissions&backend=psaw&keyword=${subreddit.value}&subreddits=${subreddit.value}")
      .response(asJson[GummysearchSubmissionsResponse])
      .send(backend)
      .flatMap { r =>
        r.body match {
          case Right(response) =>
            response.results.flatMap(MentionsMapper.map).pure[F]
          case Left(DeserializationException(body, error)) =>
            logger.error(s"error parsing gummysearch json: ${error.getMessage}\n$body") *>
              AppError.Json(s"error parsing gummysearch json: ${error.getMessage}").raiseError[F, List[Mention]]
          case Left(_) if attempt == 0 =>
            logger.error(s"exceeded maximum amount of attempts getting submissions from gummysearch") *>
              List.empty[Mention].pure[F]
          case Left(error) =>
            logger.error(s"error getting submissions from gummysearch: ${r.code}\n$error") *>
              timer.sleep(2.second) *> queryGummysearch(subreddit, attempt - 1)
        }
      }
}

object RedditClient {
  def make[F[_]: Sync: Logger: Timer: NonEmptyParallel](config: RedditConfig, backend: SttpBackend[F, Any]): F[RedditClient[F]] =
    Sync[F].delay(new LiveRedditClient[F](config, backend))
}
