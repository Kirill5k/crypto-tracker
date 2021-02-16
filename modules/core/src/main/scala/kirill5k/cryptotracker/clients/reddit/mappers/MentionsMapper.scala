package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.Submission
import kirill5k.cryptotracker.domain.{Mention, MentionSource, Ticker}

import java.net.URI
import java.time.Instant

private[reddit] object MentionsMapper {

  private val wordsFilter = List(
    "USD", "WSB", "DD"
  ).mkString("(?i).*(", "|", ").*").r

  private val tickerRegex = List(
    "^\\$[a-zA-Z]{2,4}", "CRSR", "GME", "TSLA", "PLTR", "NOK", "NCLH", "AMC", "BB", "INTC"
  ).mkString("(", "|", ")").r

  private def withoutSpecialChars(text: String): String =
      text
        .replaceAll("[@~+%\"{}?_;`—–“”!•£&#’'*|.\\[\\]]", "")
        .replaceAll("[\\\\()/,:-]", " ")
        .replaceAll(" +", " ")
        .trim

  def map(submission: Submission): List[Mention] =
    submission.title
      .split(" ")
      .map(withoutSpecialChars)
      .filterNot(wordsFilter.matches)
      .filter(tickerRegex.matches)
      .distinct
      .map { t =>
        Mention(
          Ticker(t.tail.toUpperCase),
          Instant.ofEpochSecond(submission.created_utc),
          submission.title,
          MentionSource.Reddit(submission.subreddit),
          URI.create(submission.full_link)
        )
      }
      .toList
}
