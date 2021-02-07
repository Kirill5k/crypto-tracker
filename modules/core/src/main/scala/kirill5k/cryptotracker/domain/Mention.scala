package kirill5k.cryptotracker.domain

import java.time.Instant

final case class StockSymbol(value: String) extends AnyVal
final case class Subreddit(value: String) extends AnyVal

sealed trait MentionSource

object MentionSource {
  final case class Reddit(subreddit: Subreddit) extends MentionSource
}

/*
  - context?
 */
final case class Mention(
    stock: StockSymbol,
    time: Instant,
    message: String,
    source: MentionSource,
    url: String
)
