package kirill5k.cryptotracker.services

import cats.effect.{Sync, Timer}
import fs2.Stream
import kirill5k.cryptotracker.common.stream._
import kirill5k.cryptotracker.clients.reddit.RedditClient
import kirill5k.cryptotracker.domain.{Mention, Subreddit}

import scala.concurrent.duration.FiniteDuration

trait MentionService[F[_]] {
  def liveFromReddit(subreddit: Subreddit, searchFrequency: FiniteDuration): Stream[F, Mention]
}

final private class LiveMentionService[F[_]: Sync: Timer](
    private val redditClient: RedditClient[F]
) extends MentionService[F] {

  override def liveFromReddit(subreddit: Subreddit, searchFrequency: FiniteDuration): Stream[F, Mention] =
    Stream
      .eval(redditClient.findMentions(subreddit, searchFrequency))
      .repeatEvery(searchFrequency)
      .flatMap(Stream.emits)
}

object MentionService {
  def make[F[_]: Timer: Sync](redditClient: RedditClient[F]): F[MentionService[F]] =
    Sync[F].delay(new LiveMentionService[F](redditClient))
}
