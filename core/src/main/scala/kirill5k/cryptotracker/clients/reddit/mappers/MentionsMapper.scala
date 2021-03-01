package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.{GummysearchSubmission, PushshiftSubmission}
import kirill5k.cryptotracker.domain.{Mention, MentionSource, Subreddit, Ticker}

import java.net.URI
import java.time.Instant

private[reddit] object MentionsMapper {

  private val mostCommonTickers = List(
    "AA", "ABNB", "AAPL", "APHA", "ACB", "AMC", "AMCX", "AMD", "AMZN", "ASRT", "ATVK", "ANF", "ATNX", "APTO", "AACG",
    "BOOM", "BA", "BABA", "BB", "BIDU", "BNGO",
    "CCL", "CCIV", "CRSR", "CRON", "CUK", "CFO", "CRSP", "CTRM", "CTXR", "CRM", "CRMD",
    "DIS", "DBX", "DKNG", "DNN", "DOW", "EBON", "EXPI", "ETSY", "ECOR",
    "FCEL", "FLY", "FB", "FSLY", "FUBO", "FUTU", "FSR", "GPRO", "GE", "GME", "GSAT", "GRPN", "HCMC",
    "IBKR", "ICLN", "IDEX", "IQ", "INTC", "JNJ", "JP",
    "KMPH",
    "MARA", "MGA", "MGI", "MRNA", "MVIS", "MLLLF", "MSFT", "MCD", "MU", "MGNI", "MT",
    "NAK", "NAKD", "NGAC", "NOK", "NCLH", "NIO", "NDL", "NVDA", "NET", "NTB", "NNDM", "NXE",
    "OCGN", "ONTX", "PENN", "PINS", "PLUG", "PLTR", "PLL", "PSLV", "PRPL", "PYPL", "PTON", "PFE",
    "QQQ", "QCOM", "RAIL", "RCL", "RIOT", "RIDE", "RKT", "RTX", "ROKU",
    "SCU", "SI", "SPCE", "SOS", "SE", "SEE", "SQ", "SNDL", "SPY", "SQQQ", "SKYW", "SLV", "SDC", "SBUX", "SNAP", "SAVE", "SCKT",
    "TEAM", "TDA", "TD", "TSLA", "TSNP", "TLRY", "TLT", "TSM", "TNXP", "TXMD", "TQQQ", "VACQ",
    "WEN", "WWE", "WMT", "WKHS", "XOM", "XRT", "XXII", "XTNT", "UAL", "ZMRK", "ZNGA", "ZOM"
  )

  private val wordsFilter = List(
    "USD", "WSB", "DD", "GAME", "YOLO", "CNBC", "SHIT", "WAS"
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
