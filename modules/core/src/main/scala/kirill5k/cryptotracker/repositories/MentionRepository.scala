package kirill5k.cryptotracker.repositories

import kirill5k.cryptotracker.domain.{Mention, Ticker}

import java.time.Instant

trait MentionRepository[F[_]] {
  def save(mention: Mention): F[Unit]
  def find(ticker: Ticker, from: Instant, to: Instant): F[List[Mention]]
  def findAll(from: Instant, to: Instant): F[List[Mention]]
}
