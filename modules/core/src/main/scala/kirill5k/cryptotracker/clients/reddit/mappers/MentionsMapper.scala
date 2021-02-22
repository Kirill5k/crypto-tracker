package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.Submission
import kirill5k.cryptotracker.domain.{Mention, MentionSource, Ticker}

import java.net.URI
import java.time.Instant

private[reddit] object MentionsMapper {

  private val mostCommonTickers = List(
    "AAPL", "APHA", "AMC", "AMZN", "BA", "BABA", "BB", "BIDU", "CCL", "CCIV", "CRSR", "EBON",
    "FSLY", "FUBO", "FUTU", "GE", "GME", "GSAT", "INTC",
    "MARA", "MVIS", "MLLLF", "NOK", "NCLH", "NIO", "PLUG", "PLTR",
    "QCOM", "RIOT", "RKT", "SI", "SQ", "SNDL", "SPY", "TDA", "TD", "TSLA", "TLRY", "XRT", "UAL"
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
