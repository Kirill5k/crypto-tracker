package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.Submission
import kirill5k.cryptotracker.domain.{Mention, MentionSource, Ticker}

import java.net.URI
import java.time.Instant

private[reddit] object MentionsMapper {

  private val mostCommonTickers = List(
    "AAPL", "APHA", "AMC", "AMD", "AMZN", "ASRT", "ATVK", "BA", "BABA", "BB", "BIDU", "CCL", "CCIV", "CRSR", "CRON", "CUK", "EBON",
    "DBX", "FSLY", "FUBO", "FUTU", "GE", "GME", "GSAT", "INTC",
    "KMPH",
    "MARA", "MGA", "MGI", "MVIS", "MLLLF", "MSFT",
    "NGAC", "NOK", "NCLH", "NIO", "NDL", "PLUG", "PLTR", "PSLV",
    "QCOM", "RAIL", "RIOT", "RKT", "SI", "SQ", "SNDL", "SPY", "TDA", "TD", "TSLA", "TSNP", "TLRY", "XRT", "UAL", "ZMRK"
  )

  private val wordsFilter = List(
    "USD", "WSB", "DD", "GAME", "YOLO"
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
          Ticker(t.replaceAll("\\$", "").toUpperCase),
          Instant.ofEpochSecond(submission.created_utc),
          submission.title,
          MentionSource.Reddit(submission.subreddit),
          URI.create(submission.full_link)
        )
      }
      .toList
}
