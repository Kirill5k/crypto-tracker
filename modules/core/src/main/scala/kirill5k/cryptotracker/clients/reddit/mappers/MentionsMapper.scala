package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.Submission
import kirill5k.cryptotracker.domain.{Mention, MentionSource, Ticker}

import java.time.Instant

private[reddit] object MentionsMapper {

  private def withoutSpecialChars(text: String): String =
      text
        .replaceAll("[@~+%\"{}?_;`—–“”!•£&#’'*|.\\[\\]]", "")
        .replaceAll("[\\\\()/,:-]", " ")
        .replaceAll(" +", " ")
        .trim

  private val tickerRegex = List(
    "^\\$[a-zA-Z]{2,4}"
  ).mkString("(?i)(", "|", ")").r

  def map(submission: Submission): List[Mention] =
    submission.title
      .split(" ")
      .map(withoutSpecialChars)
      .filter(tickerRegex.matches)
      .distinct
      .map { t =>
        Mention(
          Ticker(t.tail.toUpperCase),
          Instant.ofEpochSecond(submission.created_utc),
          submission.title,
          MentionSource.Reddit(submission.subreddit),
          submission.full_link
        )
      }
      .toList
}
