package kirill5k.cryptotracker.repositories

import cats.effect.{ContextShift, IO}
import kirill5k.cryptotracker.EmbeddedMongo
import kirill5k.cryptotracker.domain.{CompanyBuilder, Ticker}
import mongo4cats.client.MongoClientF
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.ExecutionContext

class CompanyRepositorySpec extends AnyWordSpec with Matchers with EmbeddedMongo {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  "CompanyRepository" when {

    "save" should {
      "company in database and find it by ticker" in {
        withEmbeddedMongoClient { client =>
          val company = CompanyBuilder.company(Ticker("TSLA"))

          val result = for {
            repo     <- CompanyRepository.make[IO](client)
            _        <- repo.save(company)
            cmp <- repo.findBy(company.ticker)
          } yield cmp

          result.map { res =>
            res mustBe Some(company)
          }
        }
      }
    }

    "findBy" should {
      "return None when item does not exist" in {
        withEmbeddedMongoClient { client =>
          val result = for {
            repo    <- CompanyRepository.make[IO](client)
            res <- repo.findBy(Ticker("FOO"))
          } yield res

          result.map { res =>
            res mustBe None
          }
        }
      }
    }

    "existsBy" should {
      "return true when mention exists by ticker and url" in {
        withEmbeddedMongoClient { client =>
          val result = for {
            repo    <- CompanyRepository.make[IO](client)
            mention = CompanyBuilder.company(Ticker("AMC"))
            _       <- repo.save(mention)
            exists <- repo.existsBy(mention.ticker)
          } yield exists

          result.map { res =>
            res mustBe true
          }
        }
      }

      "return false when mention is new" in {
        withEmbeddedMongoClient { client =>
          val result = for {
            repo    <- CompanyRepository.make[IO](client)
            mention = CompanyBuilder.company(Ticker("AMC"))
            _       <- repo.save(mention)
            exists <- repo.existsBy(Ticker("BB"))
          } yield exists

          result.map { res =>
            res mustBe false
          }
        }
      }
    }
  }

  def withEmbeddedMongoClient[A](test: MongoClientF[IO] => IO[A]): A =
    withRunningEmbeddedMongo(port = 12347) {
      MongoClientF
        .fromConnectionString[IO]("mongodb://localhost:12347")
        .use(test)
        .unsafeRunSync()
    }
}
