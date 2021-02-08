package kirill5k.cryptotracker.clients.reddit

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import kirill5k.cryptotracker.common.config.RedditConfig
import kirill5k.cryptotracker.domain.{Mention, Subreddit}
import io.circe.generic.auto._
import sttp.client3._
import sttp.model.StatusCode
import sttp.client3.circe.asJson

import scala.concurrent.duration.FiniteDuration

trait RedditClient[F[_]] {
  def findMentions(subreddit: Subreddit, duration: FiniteDuration): F[List[Mention]]
}

final private class LiveRedditClient[F[_]: Sync: Logger](
    private val config: RedditConfig,
    private val backend: SttpBackend[F, Any]
) extends RedditClient[F] {

  override def findMentions(subreddit: Subreddit, duration: FiniteDuration): F[List[Mention]] =
    basicRequest
      .get(uri"${config.baseUri}/reddit/search/submission?subreddit=${subreddit.value}&over_18=true&after=${duration.toString()}")
      .response(asJson[CexSearchResponse])
      .send(backend)
    .flatMap { r =>
      r.body match {
        case Right(response) =>
          response.pure[F]
        case Left(DeserializationException(body, error)) =>
          L.error(s"error parsing json: ${error.getMessage}\n$body") *>
            AppError.Json(s"error parsing json: ${error.getMessage}").raiseError[F, CexSearchResponse]
        case Left(HttpError(_, StatusCode.TooManyRequests)) =>
          L.warn(s"too many requests to cex. retrying") *> T.sleep(5.seconds) *> search(uri)
        case Left(error) =>
          L.error(s"error sending price query to cex: ${r.code}\n$error") *>
            T.sleep(5.second) *> search(uri)
      }
    }
}

object RedditClient {}
