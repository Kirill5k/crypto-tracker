package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.Submission
import kirill5k.cryptotracker.domain.{Mention, MentionSource, Ticker}

import java.net.URI
import java.time.Instant

private[reddit] object MentionsMapper {

  private val mostCommonTickers = List(
    "CRSR", "GME", "TSLA", "PLTR", "NOK", "NCLH", "AMC", "BB", "INTC", "QCOM", "RIOT", "RKT", "NIO", "SPY",
    "AAPL", "CCIV", "SQ", "BA", "APHA", "CCL", "GE", "UAL", "SI", "MARA", "MVIS", "TDA", "BABA", "TD", "PLUG",
    "EBON", "TLRY", "FSLY", "FUBO", "FUTU", "AMZN", "GSAT", "BIDU", "XRT", "MLLLF"
  )

  private val wordsFilter = List(
    "USD", "WSB", "DD", "GAME"
  ).mkString("(?i).*(", "|", ").*").r

  private val tickerRegex = ("^\\$[a-zA-Z]{2,5}" :: mostCommonTickers).mkString("(", "|", ")").r

  private def withoutSpecialChars(text: String): String =
      text
        .replaceAll("[@~+%\"{}?_;`—–“”!•£&#’'*|.\\[\\]]", "")
        .replaceAll("[\\\\()/,:-]", " ")
        .replaceAll(" +", " ")
        .trim

  def map(submission: Submission): List[Mention] =
    submission.title
      .split(" ")
      .filter(_.length > 1)
      .map(withoutSpecialChars)
      .filterNot(wordsFilter.matches)
      .filter(tickerRegex.matches)
      .distinct
      .map { t =>
        Mention(
          Ticker(t.replaceAll("\\$", "")),
          Instant.ofEpochSecond(submission.created_utc),
          submission.title,
          MentionSource.Reddit(submission.subreddit),
          URI.create(submission.full_link)
        )
      }
      .toList
}
