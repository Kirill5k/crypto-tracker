package kirill5k.cryptotracker.domain

import java.time.Instant

final case class Subreddit(value: String) extends AnyVal

sealed trait MentionSource

object MentionSource {
  final case class Reddit(subreddit: Subreddit) extends MentionSource
}

final case class Mention(
    ticker: Ticker,
    time: Instant,
    message: String,
    source: MentionSource,
    url: String
)
