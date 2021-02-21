package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.Submission
import kirill5k.cryptotracker.domain.{Subreddit, Ticker}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant

class MentionsMapperSpec extends AnyWordSpec with Matchers {

  "A MentionsMapper" should {

    "extract ticker mentions from submission title" in {
      val submission = makeSubmission("I think $NOK and $BB are trying to tell me something 😳 $$$ $1M $USD WSB $GAME")

      val mentions = MentionsMapper.map(submission)

      mentions.map(_.ticker) mustBe List(Ticker("NOK"), Ticker("BB"))
    }
  }

  def makeSubmission(title: String, timestamp: Instant = Instant.now()): Submission = Submission(
    "submission-1",
    timestamp.getEpochSecond,
    timestamp.getEpochSecond,
    "https://www.reddit.com/r/wallstreetbets/comments/foo/bar",
    "/r/wallstreetbets/comments/foo/bar",
    Subreddit("wallstreetbets"),
    title
  )
}
