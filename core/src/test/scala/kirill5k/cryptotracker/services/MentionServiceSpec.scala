package kirill5k.cryptotracker.services

import cats.effect.IO
import kirill5k.cryptotracker.CatsIOSpec
import kirill5k.cryptotracker.clients.reddit.RedditClient
import kirill5k.cryptotracker.domain.MentionBuilder.mention
import kirill5k.cryptotracker.domain.{Mention, MentionBuilder, Subreddit, Ticker}
import kirill5k.cryptotracker.repositories.MentionRepository

import java.net.URI
import java.time.Instant
import scala.concurrent.duration._

class MentionServiceSpec extends CatsIOSpec {

  val time = Instant.now()

  "A MentionService" should {

    "stream live ticker mentions from reddit" in {
      val (client, repository)  = mocks

      when(client.findMentions(any[Subreddit], any[FiniteDuration]))
        .thenReturn(IO.pure(List(mention(Ticker("BB")))))

      val res = for {
        service <- MentionService.make[IO](client, repository)
        mentions <- service
          .liveFromReddit(Subreddit("wallstreetbets"), 1.seconds)
          .interruptAfter(1200.millis)
          .compile
          .toList
      } yield mentions

      res.unsafeToFuture().map { mentions =>
        verify(client).findMentions(Subreddit("wallstreetbets"), 1.seconds)
        mentions must have size 2
      }
    }

    "save mention in repository" in {
      val ment = mention(Ticker("BB"))
      val (client, repository)  = mocks

      when(repository.save(any[Mention])).thenReturn(IO.unit)

      val res = for {
        service <- MentionService.make[IO](client, repository)
        result <- service.save(ment)
      } yield result

      res.unsafeToFuture().map { r =>
        verify(repository).save(ment)
        r mustBe ()
      }
    }

    "find mentions in repository" in {
      val (client, repository)  = mocks

      when(repository.findAll(any[Instant], any[Instant]))
        .thenReturn(IO.pure(List(mention(Ticker("BB")), mention(Ticker("AMC")))))

      val res = for {
        service <- MentionService.make[IO](client, repository)
        result <- service.findAll(time.minusSeconds(2), time)
      } yield result

      res.unsafeToFuture().map { r =>
        verify(repository).findAll(time.minusSeconds(2), time)
        r must have size 2
      }
    }

    "find mentions in repository by ticker" in {
      val (client, repository)  = mocks

      when(repository.findBy(any[Ticker], any[Option[Instant]], any[Option[Instant]]))
        .thenReturn(IO.pure(List(mention(Ticker("BB")), mention(Ticker("AMC")), mention(Ticker("TLRY")))))

      val res = for {
        service <- MentionService.make[IO](client, repository)
        result <- service.findBy(Ticker("BB"), Some(time.minusSeconds(2)), Some(time))
      } yield result

      res.unsafeToFuture().map { r =>
        verify(repository).findBy(Ticker("BB"), Some(time.minusSeconds(2)), Some(time))
        r must have size 3
      }
    }

    "check if mention is new" in {
      val (client, repository)  = mocks

      when(repository.existsBy(any[Ticker], any[URI])).thenReturn(IO.pure(true))

      val mention = MentionBuilder.mention(Ticker("BB"))
      val res = for {
        service <- MentionService.make[IO](client, repository)
        result <- service.isNew(mention)
      } yield result

      res.unsafeToFuture().map { r =>
        verify(repository).existsBy(mention.ticker, mention.url)
        r mustBe false
      }
    }
  }

  def mocks: (RedditClient[IO], MentionRepository[IO]) =
    (mock[RedditClient[IO]], mock[MentionRepository[IO]])
}
