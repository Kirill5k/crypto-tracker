package kirill5k.cryptotracker.services

import cats.effect.{Sync, Timer}
import cats.implicits._
import fs2.Stream
import io.chrisdavenport.log4cats.Logger
import kirill5k.cryptotracker.clients.reddit.RedditClient
import kirill5k.cryptotracker.common.Streams
import kirill5k.cryptotracker.domain.{Mention, Subreddit, Ticker}
import kirill5k.cryptotracker.repositories.MentionRepository

import java.time.Instant
import scala.concurrent.duration.FiniteDuration

trait MentionService[F[_]] {
  def liveFromReddit(subreddit: Subreddit, searchFrequency: FiniteDuration): Stream[F, Mention]
  def isNew(mention: Mention): F[Boolean]
  def save(mention: Mention): F[Unit]
  def findAll(from: Instant, to: Instant): F[List[Mention]]
  def findBy(ticker: Ticker, from: Option[Instant], to: Option[Instant]): F[List[Mention]]
}

final private class LiveMentionService[F[_]: Sync: Timer](
    private val redditClient: RedditClient[F],
    private val mentionRepository: MentionRepository[F]
)(implicit val logger: Logger[F])
    extends MentionService[F] {

  override def liveFromReddit(subreddit: Subreddit, searchFrequency: FiniteDuration): Stream[F, Mention] =
    Stream
      .repeatEval(redditClient.findMentions(subreddit, searchFrequency))
      .zipLeft(Streams.fixedRateImmediate[F](searchFrequency))
      .evalTap(ms => logger.info(s"returned ${ms.size} new stock mentions from reddit"))
      .flatMap(Stream.emits)

  override def save(mention: Mention): F[Unit] =
    mentionRepository.save(mention)

  override def findAll(from: Instant, to: Instant): F[List[Mention]] =
    mentionRepository.findAll(from, to)

  override def findBy(ticker: Ticker, from: Option[Instant], to: Option[Instant]): F[List[Mention]] =
    mentionRepository.findBy(ticker, from, to)

  override def isNew(mention: Mention): F[Boolean] =
    mentionRepository
      .existsBy(mention.ticker, mention.url)
      .map(!_)
}

object MentionService {
  def make[F[_]: Timer: Sync: Logger](redditClient: RedditClient[F], mentionRepository: MentionRepository[F]): F[MentionService[F]] =
    Sync[F].delay(new LiveMentionService[F](redditClient, mentionRepository))
}
