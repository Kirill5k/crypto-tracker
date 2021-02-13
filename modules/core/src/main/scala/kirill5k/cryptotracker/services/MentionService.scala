package kirill5k.cryptotracker.services

import cats.effect.{Sync, Timer}
import fs2.Stream
import io.chrisdavenport.log4cats.Logger
import kirill5k.cryptotracker.clients.reddit.RedditClient
import kirill5k.cryptotracker.common.Streams
import kirill5k.cryptotracker.domain.{Mention, Subreddit}

import scala.concurrent.duration.FiniteDuration

trait MentionService[F[_]] {
  def liveFromReddit(subreddit: Subreddit, searchFrequency: FiniteDuration): Stream[F, Mention]
}

final private class LiveMentionService[F[_]: Sync: Timer](
    private val redditClient: RedditClient[F]
)(implicit val logger: Logger[F]) extends MentionService[F] {

  override def liveFromReddit(subreddit: Subreddit, searchFrequency: FiniteDuration): Stream[F, Mention] =
    Stream
      .repeatEval(redditClient.findMentions(subreddit, searchFrequency))
      .zipLeft(Streams.fixedRateImmediate[F](searchFrequency))
      .evalTap(ms => logger.info(s"returned ${ms.size} new stock mentions from reddit"))
      .flatMap(Stream.emits)
}

object MentionService {
  def make[F[_]: Timer: Sync: Logger](redditClient: RedditClient[F]): F[MentionService[F]] =
    Sync[F].delay(new LiveMentionService[F](redditClient))
}
