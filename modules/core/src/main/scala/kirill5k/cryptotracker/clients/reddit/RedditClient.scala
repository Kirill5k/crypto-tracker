package kirill5k.cryptotracker.clients.reddit

import cats.effect.{Sync, Timer}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import kirill5k.cryptotracker.common.config.RedditConfig
import kirill5k.cryptotracker.domain.{Mention, Subreddit}
import io.circe.generic.auto._
import kirill5k.cryptotracker.clients.reddit.mappers.MentionsMapper
import kirill5k.cryptotracker.clients.reddit.responses.RedditSubmissionsResponse
import kirill5k.cryptotracker.common.json._
import kirill5k.cryptotracker.common.errors.AppError
import sttp.client3._
import sttp.client3.circe.asJson

import scala.concurrent.duration._

trait RedditClient[F[_]] {
  def findMentions(subreddit: Subreddit, duration: FiniteDuration): F[List[Mention]]
}

final private class LiveRedditClient[F[_]: Sync](
    private val config: RedditConfig,
    private val backend: SttpBackend[F, Any]
)(implicit
    logger: Logger[F],
    timer: Timer[F]
) extends RedditClient[F] {

  override def findMentions(subreddit: Subreddit, duration: FiniteDuration): F[List[Mention]] = {
    val dur = duration.toString().replaceFirst(" ", "").replaceFirst("(?<=[a-z])\\w+", "")
    basicRequest
      .get(uri"${config.baseUri}/reddit/search/submission?subreddit=${subreddit.value}&over_18=true&after=$dur")
      .response(asJson[RedditSubmissionsResponse])
      .send(backend)
      .flatMap { r =>
        r.body match {
          case Right(response) =>
            response.data.flatMap(MentionsMapper.map).pure[F]
          case Left(DeserializationException(body, error)) =>
            logger.error(s"error parsing json: ${error.getMessage}\n$body") *>
              AppError.Json(s"error parsing json: ${error.getMessage}").raiseError[F, List[Mention]]
          case Left(error) =>
            logger.error(s"error get submissions from reddit: ${r.code}\n$error") *>
              timer.sleep(5.second) *> findMentions(subreddit, duration + 5.second)
        }
      }
  }
}

object RedditClient {
  def make[F[_]: Sync: Logger: Timer](config: RedditConfig, backend: SttpBackend[F, Any]): F[RedditClient[F]] =
    Sync[F].delay(new LiveRedditClient[F](config, backend))
}
