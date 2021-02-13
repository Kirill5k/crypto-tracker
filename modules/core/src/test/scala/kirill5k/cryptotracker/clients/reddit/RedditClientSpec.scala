package kirill5k.cryptotracker.clients.reddit

import cats.effect.IO
import kirill5k.cryptotracker.RequestOps._
import kirill5k.cryptotracker.SttpClientSpec
import kirill5k.cryptotracker.common.config.RedditConfig
import kirill5k.cryptotracker.domain.{Subreddit, Ticker}
import sttp.client3.{Response, SttpBackend}

import scala.concurrent.duration._

class RedditClientSpec extends SttpClientSpec {

  val subreddit = Subreddit("WallStreetBets")
  val config = RedditConfig("http://reddit.com", 5.minutes, List(subreddit))

  "A RedditClient" should {

    "return list of stock/crypto mentions from a given subreddit" in {
      val submissionsEndpoint = "reddit/search/submission"
      val params = Map("after" -> "5m", "subreddit" -> "WallStreetBets", "over_18" -> "true")
      val testingBackend: SttpBackend[IO, Any] = backendStub
        .whenRequestMatchesPartial {
          case r if r.isGet && r.isGoingTo(s"reddit.com/$submissionsEndpoint") && r.hasParams(params) =>
            Response.ok(json("reddit/submissions-response.json"))
          case _ => throw new RuntimeException()
        }

      val telegramClient = RedditClient.make[IO](config, testingBackend)
      val result = telegramClient.flatMap(_.findMentions(subreddit, 5.minutes))

      result.unsafeToFuture().map { mentions =>
        mentions.map(_.ticker) mustBe List(Ticker("NOK"), Ticker("GME"), Ticker("CRSR"), Ticker("ZOM"))
      }
    }
  }
}
