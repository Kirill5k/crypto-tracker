package kirill5k.cryptotracker.domain

import java.net.URI
import java.time.Instant
import scala.util.Random

object MentionBuilder {

  def mention(
      ticker: Ticker = Ticker(Random.alphanumeric.take(4).mkString("")),
      time: Instant = Instant.now()
  ): Mention =
    Mention(
      ticker,
      time,
      s"Why ${ticker.value} is gonna blow its load SOON 15k YOLO",
      MentionSource.Reddit(Subreddit("WallStreetBets")),
      URI.create("http://reddit.com")
    )
}
