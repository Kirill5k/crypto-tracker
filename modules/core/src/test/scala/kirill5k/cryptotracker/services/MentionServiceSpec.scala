package kirill5k.cryptotracker.services

import cats.effect.IO
import kirill5k.cryptotracker.CatsIOSpec
import kirill5k.cryptotracker.clients.reddit.RedditClient
import kirill5k.cryptotracker.domain.MentionBuilder.mention
import kirill5k.cryptotracker.domain.{Subreddit, Ticker}

import scala.concurrent.duration._

class MentionServiceSpec extends CatsIOSpec {

  "A MentionService" should {

    "stream live ticker mentions from reddit" in {
      val mentions = List(mention(Ticker("BB")), mention(Ticker("AMD")))
      val (client)  = mocks

      when(client.findMentions(any[Subreddit], any[FiniteDuration]))
        .thenReturn(IO.pure(mentions))
        .andThen(IO.pure(mentions))

      val res = for {
        service <- MentionService.make[IO](client)
        mentions <- service
          .liveFromReddit(Subreddit("wallstreetbets"), 1.seconds)
          .interruptAfter(1500.millis)
          .compile
          .toList
      } yield mentions

      res.unsafeToFuture().map { returnedMentions =>
        returnedMentions mustBe mentions ::: mentions
      }
    }
  }

  def mocks: (RedditClient[IO]) =
    mock[RedditClient[IO]]
}
