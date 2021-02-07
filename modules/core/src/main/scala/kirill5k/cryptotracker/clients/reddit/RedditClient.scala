package kirill5k.cryptotracker.clients.reddit

import cats.effect.Sync
import io.chrisdavenport.log4cats.Logger
import kirill5k.cryptotracker.common.config.RedditConfig
import kirill5k.cryptotracker.domain.{Mention, Subreddit}
import sttp.client3.SttpBackend

import scala.concurrent.duration.FiniteDuration

trait RedditClient[F[_]] {
  def findMentions(subreddit: Subreddit, duration: FiniteDuration): F[List[Mention]]
}

final private class LiveRedditClient[F[_]: Sync: Logger](
    private val config: RedditConfig,
    private val backend: SttpBackend[F, Any]
) extends RedditClient[F] {

  override def findMentions(subreddit: Subreddit, duration: FiniteDuration): F[List[Mention]] = ???
}

object RedditClient {}
