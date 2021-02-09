package kirill5k.cryptotracker.domain

import java.time.Instant

object MentionBuilder {

  def mention(
      ticker: Ticker = Ticker("AAPL"),
      time: Instant = Instant.now()
  ): Mention =
    Mention(
      ticker,
      time,
      s"Why ${ticker.value} is gonna blow its load SOON 15k YOLO",
      MentionSource.Reddit(Subreddit("WallStreetBets")),
      "http://reddit.com"
    )
}
