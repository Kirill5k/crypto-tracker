package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.{GummysearchSubmission, PushshiftSubmission}
import kirill5k.cryptotracker.domain.MentionSource.Reddit
import kirill5k.cryptotracker.domain.{Mention, Subreddit, Ticker}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.net.URI
import java.time.Instant

class MentionsMapperSpec extends AnyWordSpec with Matchers {

  "A MentionsMapper" should {

    "extract ticker mentions from pushshift submission title" in {
      val submission = makePushshiftSubmission("I think $NOK and $BB are trying to tell me something 😳 $$$ $1M $USD WSB $GAME")

      val mentions = MentionsMapper.map(submission)

      mentions.map(_.ticker) mustBe List(Ticker("NOK"), Ticker("BB"))
    }

    "extract ticker mentions from gummysearch submission title" in {
      val submission = makePushshiftSubmission("Too late to jump on GME train")

      val mentions = MentionsMapper.map(submission)

      mentions must have size 1
      mentions.head mustBe Mention(
        Ticker("GME"),
        Instant.ofEpochSecond(1614284715),
        "Too late to jump on GME train",
        Reddit(Subreddit("wallstreetbets")),
        URI.create("https://www.reddit.com/r/wallstreetbets/comments/foo/bar")
      )
    }
  }

  def makeGummysearchSubmission(title: String): GummysearchSubmission =
    GummysearchSubmission(
      title,
      "https://www.reddit.com/r/wallstreetbets",
      1614284715.0,
      "r/wallstreetbets",
      "/r/wallstreetbets/comments/foo/bar/"
    )

  def makePushshiftSubmission(title: String): PushshiftSubmission =
    PushshiftSubmission(
      "submission-1",
      1614284715,
      1614284715,
      "https://www.reddit.com/r/wallstreetbets/comments/foo/bar",
      "/r/wallstreetbets/comments/foo/bar",
      Subreddit("wallstreetbets"),
      title
    )
}
