package kirill5k.cryptotracker.clients.reddit.mappers

import kirill5k.cryptotracker.clients.reddit.responses.{GummysearchSubmission, PushshiftSubmission}
import kirill5k.cryptotracker.domain.{Mention, MentionSource, Subreddit, Ticker}

import java.net.URI
import java.time.Instant

private[reddit] object MentionsMapper {

  private val mostCommonTickers = List(
    "ANPC", "AA", "AI", "AAL", "ABNB", "AAPL", "APHA", "AESE", "ACB", "AMC", "AGTC", "AMCX", "AMD", "API", "AMZN", "ASRT", "ASO", "ATVK", "ANF", "ATNX", "APTO", "AACG", "AN",
    "BFLY", "BOOM", "BA", "BBC", "BABA", "BT", "BB", "BIDU", "BNGO", "BAC", "BP", "BUD", "BIGC",
    "CO", "CC", "CR", "CCIV", "CLF", "COST", "CHWY", "CALM", "CCC", "CCL", "CCIV", "CRSR", "COTY", "COST", "CRON", "CPE", "CUK", "CNK", "CFO", "CRSP", "CTRM", "CTXR", "CRM", "CRMD", "COHN", "CLVS", "CVS",
    "DASH", "DDF", "DIS", "DBX", "DKNG", "DNN", "DOW", "DISH", "EBON", "ET", "EXPI", "ETSY", "ECOR", "EYES", "EAT", "EXPR", "EA", "ERIC", "ELY",
    "FDX", "FUN", "FCEL", "FLY", "FB", "FSLY", "FUBO", "FUTU", "FSR", "FBIO", "F",
    "GOEV", "GOGO", "GEVO", "GPRO", "GE", "GME", "GTE", "GSAT", "GRPN", "GTT", "GHIV", "GMBL", "GLD", "GL", "GMED", "GNUS",
    "HIMS", "HIG", "HOME", "HYLN", "HEAR", "HCMC", "IBM", "IRS", "IBKR", "ICLN", "IDEX", "IQ", "IVR", "INTC", "JNJ", "JD", "JP", "JOE", "JPM", "JOB", "JFIN", "JMIA",
    "KIDS", "KEY", "KNSA", "KBAL", "KMI", "KMPH", "LC", "LUMN", "LEG", "LAZR", "LKCO", "LYFT", "LULU", "LOW", "LMT",
    "MP", "MA", "MAN", "MAXR", "MRK", "MARA", "MGA", "MGI", "MRO", "MRNA", "MVIS", "MLLLF", "MSFT", "MCD", "MU", "MGNI", "MT", "MIK", "MSM", "MREO", "MO",
    "NKLA", "NAK", "NAKD", "NGAC", "NOK", "NCLH", "NIO", "NDL", "NVDA", "NVAX", "NET", "NTB", "NNDM", "NXE", "NVTA", "NKE",
    "OCGN", "ONTX", "OVID", "OPTT", "OTRK", "ORCL", "OXY", "OLD", "OPEN", "OLB",
    "PCG", "PTR", "PLAY", "PENN", "CRPINS", "PLUG", "PLTR", "PLL", "PPT", "PSLV", "PRPL", "PYPL", "PTON", "PFE", "PDAC", "PSTH",
    "QQQ", "QCOM", "RAIL", "RBLX", "RCL", "RIOT", "RIDE", "RKT", "RTX", "ROKU", "RBC", "RIG", "RING", "RC",
    "SUMO", "SAFE", "SAVA", "SENS", "SLGG", "SKT", "SCU", "SI", "SPCE", "SWBI", "SHIP", "SOS", "SE", "SEE", "SQ", "SNDL", "SPY", "SQQQ", "SKYW", "SLV", "SDC", "SBUX", "SNAP", "SAVE", "SCKT", "SNOW", "STK", "SP",
    "THC", "TKAT", "TRXC", "TEAM", "TDA", "TD", "TGT", "TSLA", "TURN", "TSNP", "TECH", "TTD", "TLRY", "TLT", "TSM", "TNXP", "TXMD", "TQQQ", "TELL", "TRCH", "THO", "TRVG",
    "VACQ", "VRTV", "VALE", "VIAC", "VUZI",
    "WFC", "WSM", "WKEY", "WIMI", "WFG", "WORK", "WEN", "WWE", "WMT", "WKHS", "WTI", "WBAI", "XOM", "XRT", "XPEV", "XXII", "XTNT", "X",
    "UUUU", "UPST", "UBER", "UI", "UNFI", "USMC", "UBS", "UMC", "UAL", "UWMC", "YGMZ", "ZMRK", "ZNGA", "ZOM"
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
