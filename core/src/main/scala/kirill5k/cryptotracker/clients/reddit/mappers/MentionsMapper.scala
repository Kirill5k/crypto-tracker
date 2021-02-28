package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.{GummysearchSubmission, PushshiftSubmission}
import kirill5k.cryptotracker.domain.{Mention, MentionSource, Subreddit, Ticker}

import java.net.URI
import java.time.Instant

private[reddit] object MentionsMapper {

  private val mostCommonTickers = List(
    "AA", "ABNB", "AAPL", "APHA", "AMC", "AMD", "AMZN", "ASRT", "ATVK",
    "BOOM", "BA", "BABA", "BB", "BIDU", "CCL", "CCIV", "CRSR", "CRON", "CUK", "CFO", "CRSP",
    "DIS", "DBX", "DKNG", "EBON", "EXPI",
    "FLY", "FB", "FSLY", "FUBO", "FUTU", "FSR", "GE", "GME", "GSAT",
    "HCMC", "IQ", "INTC", "JNJ",
    "KMPH",
    "MARA", "MGA", "MGI", "MRNA", "MVIS", "MLLLF", "MSFT", "MCD",
    "NGAC", "NOK", "NCLH", "NIO", "NDL", "NVDA", "NET",
    "PLUG", "PLTR", "PSLV", "PYPL",
    "QQQ", "QCOM", "RAIL", "RIOT", "RIDE", "RKT", "RTX", "ROKU",
    "SI", "SPCE", "SOS", "SE", "SQ", "SNDL", "SPY", "SQQQ", "SKYW", "SLV", "SDC", "SBUX",
    "TDA", "TD", "TSLA", "TSNP", "TLRY", "TLT", "TSM",
    "WEN", "WMT", "WKHS", "XOM", "XRT", "UAL", "ZMRK", "ZNGA", "ZOM"
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
