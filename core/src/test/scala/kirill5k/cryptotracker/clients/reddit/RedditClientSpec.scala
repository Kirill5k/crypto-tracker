package kirill5k.cryptotracker.clients.reddit

import cats.effect.{Clock, IO, Timer}
import kirill5k.cryptotracker.RequestOps._
import kirill5k.cryptotracker.SttpClientSpec
import kirill5k.cryptotracker.common.config.RedditConfig
import kirill5k.cryptotracker.domain.{Subreddit, Ticker}
import sttp.client3.{Response, SttpBackend}

import java.time.Instant
import scala.concurrent.duration._

class RedditClientSpec extends SttpClientSpec {

  val subreddit = Subreddit("WallStreetBets")
  val config    = RedditConfig("http://pushshift.com", "http://gummysearch.com", 5.minutes, List(subreddit))
  val timestamp = Instant.parse("2020-01-01T00:25:00Z")

  "A RedditClient" should {

    "return list of stock/crypto mentions in a given subreddit from pushshift" in {
      implicit val timer = mockTimer(timestamp.getEpochSecond)

      val submissionsEndpoint = "reddit/submission/search"
      val params              = Map("subreddit" -> "WallStreetBets", "after" -> "1577838000")
      val testingBackend: SttpBackend[IO, Any] = backendStub
        .whenRequestMatchesPartial {
          case r if r.isGet && r.isGoingTo(s"pushshift.com/$submissionsEndpoint") && r.hasParams(params) =>
            Response.ok(json("reddit/pushshift-submissions-response.json"))
          case r if r.isGet && r.hasHost("gummysearch.com") =>
            Response.ok("""{"results":[]}""")
          case r => throw new RuntimeException(r.uri.toString())
        }

      val telegramClient = RedditClient.make[IO](config, testingBackend)
      val result         = telegramClient.flatMap(_.findMentions(subreddit, 5.minutes))

      result.unsafeToFuture().map { mentions =>
        mentions.map(_.ticker) mustBe List(
          Ticker("NOK"),
          Ticker("GME"),
          Ticker("CRSR"),
          Ticker("QCOM"),
          Ticker("ZOM"),
          Ticker("GOEV"),
          Ticker("AMC"),
          Ticker("GME"),
          Ticker("GME"),
          Ticker("PLTR"),
          Ticker("AMC")
        )
      }
    }

    "return list of stock/crypto mentions in a given subreddit from gummysearch" in {
      implicit val timer = mockTimer(timestamp.getEpochSecond)

      val submissionsEndpoint = "api/v1/reddit/submissions"
      val params              = Map("subreddits" -> "WallStreetBets", "type" -> "submissions", "backend" -> "praw", "keyword" -> "WallStreetBets")
      val testingBackend: SttpBackend[IO, Any] = backendStub
        .whenRequestMatchesPartial {
          case r if r.isGet && r.isGoingTo(s"gummysearch.com/$submissionsEndpoint") && r.hasParams(params) =>
            Response.ok(json("reddit/gummysearch-submissions-response.json"))
          case r if r.isGet && r.hasHost("pushshift.com") =>
            Response.ok("""{"data":[]}""")
          case r => throw new RuntimeException(r.uri.toString())
        }

      val telegramClient = RedditClient.make[IO](config, testingBackend)
      val result         = telegramClient.flatMap(_.findMentions(subreddit, 5.minutes))

      result.unsafeToFuture().map { mentions =>
        mentions must have size 22
      }
    }
  }

  def mockTimer(timeSeconds: Long): Timer[IO] = new Timer[IO] {
    override def clock: Clock[IO] = new Clock[IO] {
      override def realTime(unit: TimeUnit): IO[Long] = IO.pure(timeSeconds)
      override def monotonic(unit: TimeUnit): IO[Long] = IO.pure(timeSeconds)
    }
    override def sleep(duration: FiniteDuration): IO[Unit] = IO.unit
  }
}
