package kirill5k.cryptotracker.repositories

import cats.effect.Concurrent
import cats.implicits._
import kirill5k.cryptotracker.domain.{Mention, Ticker}
import kirill5k.cryptotracker.repositories.entities.MentionEntity
import mongo4cats.client.MongoClientF
import mongo4cats.database.MongoCollectionF
import org.mongodb.scala.Document
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.{Filters, Sorts}

import java.net.URI
import java.time.Instant

trait MentionRepository[F[_]] {
  def save(mention: Mention): F[Unit]
  def existsBy(ticker: Ticker, url: URI): F[Boolean]
  def findBy(ticker: Ticker, from: Option[Instant], to: Option[Instant]): F[List[Mention]]
  def findAll(from: Instant, to: Instant): F[List[Mention]]
}

final private class LiveMentionRepository[F[_]: Concurrent](
    private val collection: MongoCollectionF[MentionEntity]
) extends MentionRepository[F] {

  override def save(mention: Mention): F[Unit] =
    collection.insertOne(MentionEntity.from(mention)).void

  override def findBy(ticker: Ticker, from: Option[Instant], to: Option[Instant]): F[List[Mention]] =
    find(Some(ticker), from, to)

  override def findAll(from: Instant, to: Instant): F[List[Mention]] =
    find(None, Some(from), Some(to))

  private def find(ticker: Option[Ticker], from: Option[Instant], to: Option[Instant]): F[List[Mention]] =
    collection.find
      .filter(tickerDateRangeSelector(ticker, from, to))
      .sort(Sorts.descending("time"))
      .all[F]
      .map(_.map(_.toDomain).toList)

  private def tickerDateRangeSelector(ticker: Option[Ticker], from: Option[Instant], to: Option[Instant]): Bson = {
    val fromFilter = from.map(d => Filters.gte("time", d))
    val toFilter   = to.map(d => Filters.lt("time", d))
    val filters    = List(fromFilter, toFilter).flatten
    val dateFilter = if (filters.nonEmpty) Filters.and(filters: _*) else Document()
    val tickerFilter = ticker.fold[Bson](Document())(t => Filters.equal("ticker", t))
    Filters.and(dateFilter, tickerFilter)
  }

  override def existsBy(ticker: Ticker, url: URI): F[Boolean] =
    collection
      .count[F](Filters.and(Filters.equal("ticker", ticker), Filters.equal("url", url.toString)))
      .map(_ > 0)
}

object MentionRepository {

  def make[F[_]: Concurrent](client: MongoClientF[F]): F[MentionRepository[F]] =
    client
      .getDatabase("crypto-tracker")
      .flatMap(_.getCollection[MentionEntity]("mentions", MentionEntity.codec))
      .map(c => new LiveMentionRepository[F](c))
}
