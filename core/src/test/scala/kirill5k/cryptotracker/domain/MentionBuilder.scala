package kirill5k.cryptotracker.domain

import java.net.URI
import java.time.Instant
import java.time.temporal.ChronoField.MILLI_OF_SECOND

object MentionBuilder {

  def mention(
      ticker: Ticker,
      time: Instant = Instant.now().`with`(MILLI_OF_SECOND, 0)
  ): Mention =
    Mention(
      ticker,
      time,
      s"Why ${ticker.value} is gonna blow its load SOON 15k YOLO",
      MentionSource.Reddit(Subreddit("WallStreetBets")),
      URI.create("http://reddit.com/foo/bar")
    )
}
