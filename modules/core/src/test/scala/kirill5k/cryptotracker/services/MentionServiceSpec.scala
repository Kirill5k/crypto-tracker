package kirill5k.cryptotracker.services

import cats.effect.IO
import kirill5k.cryptotracker.CatsIOSpec
import kirill5k.cryptotracker.clients.reddit.RedditClient
import kirill5k.cryptotracker.domain.MentionBuilder.mention
import kirill5k.cryptotracker.domain.Subreddit

import scala.concurrent.duration._

class MentionServiceSpec extends CatsIOSpec {

  "A MentionService" should {

    "stream live ticker mentions from reddit" in {
      val (client)  = mocks

      when(client.findMentions(any[Subreddit], any[FiniteDuration]))
        .thenReturn(IO.pure(List(mention())))

      val res = for {
        service <- MentionService.make[IO](client)
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
  }

  def mocks: (RedditClient[IO]) =
    mock[RedditClient[IO]]
}
