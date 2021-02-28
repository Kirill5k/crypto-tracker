package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.{GummysearchSubmission, PushshiftSubmission}
import kirill5k.cryptotracker.domain.{Mention, MentionSource, Subreddit, Ticker}

import java.net.URI
import java.time.Instant

private[reddit] object MentionsMapper {

  private val mostCommonTickers = List(
    "ABNB", "AAPL", "APHA", "AMC", "AMD", "AMZN", "ASRT", "ATVK",
    "BA", "BABA", "BB", "BIDU", "CCL", "CCIV", "CRSR", "CRON", "CUK",
    "DBX", "DKNG", "EBON", "FSLY", "FUBO", "FUTU", "GE", "GME", "GSAT", "INTC", "HCMC",
    "KMPH",
    "MARA", "MGA", "MGI", "MRNA", "MVIS", "MLLLF", "MSFT",
    "NGAC", "NOK", "NCLH", "NIO", "NDL", "NVDA", "PLUG", "PLTR", "PSLV",
    "QQQ", "QCOM", "RAIL", "RIOT", "RKT", "SI", "SPCE", "SQ", "SNDL", "SPY",
    "TDA", "TD", "TSLA", "TSNP", "TLRY", "WKHS", "XRT", "UAL", "ZMRK"
  )

  private val wordsFilter = List(
    "USD", "WSB", "DD", "GAME", "YOLO", "CNBC", "SHIT"
  ).mkString("(?i).*(", "|", ").*").r

  private val tickerRegex = ("^\\$[a-zA-Z]{2,5}" :: mostCommonTickers).mkString("(", "|", ")").r

  private def withoutSpecialChars(text: String): String =
    text
      .replaceAll("[@~+%\"{}?_;`—–“”!•£&#’'*|.\\[\\]]", "")
      .replaceAll("[\\\\()/,:-]", " ")
      .replaceAll(" +", " ")
      .trim

  private def parseTickers(title: String): List[String] =
    title
      .split(" ")
      .filter(_.length > 1)
      .map(withoutSpecialChars)
      .filterNot(wordsFilter.matches)
      .filter(tickerRegex.matches)
      .distinct
      .toList

  def map(submission: PushshiftSubmission): List[Mention] =
    parseTickers(submission.title)
      .map { t =>
        Mention(
          Ticker(t.replaceAll("\\$", "").toUpperCase),
          Instant.ofEpochSecond(submission.created_utc),
          submission.title,
          MentionSource.Reddit(submission.subreddit),
          URI.create(submission.full_link)
        )
      }

  def map(submission: GummysearchSubmission): List[Mention] =
    parseTickers(submission.title)
      .map { t =>
        Mention(
          Ticker(t.replaceAll("\\$", "").toUpperCase),
          Instant.ofEpochSecond(submission.timestamp_utc.toLong),
          submission.title,
          MentionSource.Reddit(Subreddit(submission.subreddit_name.substring(2))),
          URI.create(s"https://www.reddit.com${submission.link}")
        )
      }
}
