package kirill5k.cryptotracker.repositories

import cats.effect.Concurrent
import cats.implicits._
import kirill5k.cryptotracker.domain.{Mention, Ticker}
import kirill5k.cryptotracker.repositories.entities.MentionEntity
import mongo4cats.client.MongoClientF
import mongo4cats.database.MongoCollectionF
import org.mongodb.scala.model.{Filters, Sorts}

import java.time.Instant

trait MentionRepository[F[_]] {
  def save(mention: Mention): F[Unit]
  def find(ticker: Ticker, from: Instant, to: Instant): F[List[Mention]]
  def findAll(from: Instant, to: Instant): F[List[Mention]]
}

final private class LiveMentionRepository[F[_]: Concurrent](
    private val collection: MongoCollectionF[MentionEntity]
) extends MentionRepository[F] {

  override def save(mention: Mention): F[Unit] =
    collection.insertOne(MentionEntity.from(mention)).void

  override def find(ticker: Ticker, from: Instant, to: Instant): F[List[Mention]] = ???

  override def findAll(from: Instant, to: Instant): F[List[Mention]] = {
    val fromFilter = Filters.gte("date", from)
    val toFilter   = Filters.lt("time", to)
    collection.find
      .filter(Filters.and(fromFilter, toFilter))
      .sort(Sorts.descending("time"))
      .all[F]
      .map(_.map(_.toDomain).toList)
  }
}

object MentionRepository {

  def make[F[_]: Concurrent](client: MongoClientF[F]): F[MentionRepository[F]] =
    client
      .getDatabase("crypto-tracker")
      .flatMap(_.getCollection[MentionEntity]("mentions", MentionEntity.codec))
      .map(c => new LiveMentionRepository[F](c))
}
