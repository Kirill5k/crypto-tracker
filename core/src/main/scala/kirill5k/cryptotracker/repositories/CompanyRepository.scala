package kirill5k.cryptotracker.repositories

import cats.effect.Concurrent
import cats.implicits._
import kirill5k.cryptotracker.domain.{Company, Ticker}
import kirill5k.cryptotracker.repositories.entities.CompanyEntity
import mongo4cats.client.MongoClientF
import mongo4cats.database.MongoCollectionF
import org.mongodb.scala.model.Filters

trait CompanyRepository[F[_]] {
  def save(company: Company): F[Unit]
  def findBy(ticker: Ticker): F[Option[Company]]
  def existsBy(ticker: Ticker): F[Boolean]
}

final private class LiveCompanyRepository[F[_]: Concurrent](
    private val collection: MongoCollectionF[CompanyEntity]
) extends CompanyRepository[F] {

  override def save(company: Company): F[Unit] =
    collection.insertOne(CompanyEntity.from(company)).void

  override def findBy(ticker: Ticker): F[Option[Company]] =
    collection.find
      .filter(Filters.equal("ticker", ticker.value))
      .first[F]
      .map(r => Option(r).map(_.toDomain))

  override def existsBy(ticker: Ticker): F[Boolean] =
    collection
      .count[F](Filters.equal("ticker", ticker.value))
      .map(_ > 0)
}

object CompanyRepository {

  def make[F[_]: Concurrent](client: MongoClientF[F]): F[CompanyRepository[F]] =
    client
      .getDatabase("crypto-tracker")
      .flatMap(_.getCollection[CompanyEntity]("companies", CompanyEntity.codec))
      .map(c => new LiveCompanyRepository[F](c))
}
