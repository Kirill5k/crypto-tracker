package kirill5k.cryptotracker.tasks

import cats.effect.{Concurrent, Sync}
import kirill5k.cryptotracker.common.config.RedditConfig
import kirill5k.cryptotracker.services.MentionService
import fs2.Stream

trait MentionsFinder[F[_]] extends Task[F] {}

final private class RedditMentionsFinder[F[_]: Concurrent](
    private val redditConfig: RedditConfig,
    private val mentionService: MentionService[F]
) extends MentionsFinder[F] {

  override def run(): fs2.Stream[F, Unit] =
    Stream
      .emits(redditConfig.subreddits)
      .map(sub => mentionService.liveFromReddit(sub, redditConfig.searchPeriod))
      .parJoinUnbounded
      .drain

}

object MentionsFinder {
  def reddit[F[_]: Concurrent](redditConfig: RedditConfig, mentionService: MentionService[F]): F[Task[F]] =
    Sync[F].delay(new RedditMentionsFinder[F](redditConfig, mentionService))
}
