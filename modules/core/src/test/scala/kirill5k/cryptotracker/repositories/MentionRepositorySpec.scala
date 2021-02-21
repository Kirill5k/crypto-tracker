package kirill5k.cryptotracker.repositories

import cats.effect.{ContextShift, IO}
import kirill5k.cryptotracker.EmbeddedMongo
import kirill5k.cryptotracker.domain.{MentionBuilder, Ticker}
import mongo4cats.client.MongoClientF
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant
import scala.concurrent.ExecutionContext

class MentionRepositorySpec extends AnyWordSpec with Matchers with EmbeddedMongo {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  "MentionRepository" when {

    "save" should {
      "mentions in database" in {
        withEmbeddedMongoClient { client =>
          val mention = MentionBuilder.mention(Ticker("TSLA"))

          val result = for {
            repo     <- MentionRepository.make[IO](client)
            _        <- repo.save(mention)
            mentions <- repo.findAll(Instant.now().minusSeconds(60), Instant.now())
          } yield mentions

          result.map { res =>
            res mustBe List(mention)
          }
        }
      }
    }

    "existsBy" should {
      "return true when mention exists by ticker and url" in {
        withEmbeddedMongoClient { client =>
          val result = for {
            repo    <- MentionRepository.make[IO](client)
            mention = MentionBuilder.mention(Ticker("AMC"))
            _       <- repo.save(mention)
            exists <- repo.existsBy(mention.ticker, mention.url)
          } yield exists

          result.map { res =>
            res mustBe true
          }
        }
      }

      "return false when mention is new" in {
        withEmbeddedMongoClient { client =>
          val result = for {
            repo    <- MentionRepository.make[IO](client)
            mention = MentionBuilder.mention(Ticker("AMC"))
            _       <- repo.save(mention)
            exists <- repo.existsBy(Ticker("BB"), mention.url)
          } yield exists

          result.map { res =>
            res mustBe false
          }
        }
      }
    }

    "findBy" should {
      "find mention by ticker" in {
        withEmbeddedMongoClient { client =>
          val result = for {
            repo    <- MentionRepository.make[IO](client)
            _       <- repo.save(MentionBuilder.mention(Ticker("TSLA")))
            _       <- repo.save(MentionBuilder.mention(Ticker("BB")))
            _       <- repo.save(MentionBuilder.mention(Ticker("AMC")))
            mention <- repo.findBy(Ticker("BB"), None, None)
          } yield mention

          result.map { res =>
            res must have size 1
            res.head.ticker mustBe Ticker("BB")
          }
        }
      }

      "return empty collection when outside date range" in {
        withEmbeddedMongoClient { client =>
          val result = for {
            repo    <- MentionRepository.make[IO](client)
            _       <- repo.save(MentionBuilder.mention(Ticker("TSLA")))
            _       <- repo.save(MentionBuilder.mention(Ticker("BB")))
            _       <- repo.save(MentionBuilder.mention(Ticker("AMC")))
            now = Instant.now()
            mention <- repo.findBy(Ticker("BB"), Some(now.minusSeconds(120)), Some(now.minusSeconds(60)))
          } yield mention

          result.map { res =>
            res must have size 0
          }
        }
      }
    }
  }

  def withEmbeddedMongoClient[A](test: MongoClientF[IO] => IO[A]): A =
    withRunningEmbeddedMongo() {
      MongoClientF
        .fromConnectionString[IO]("mongodb://localhost:12345")
        .use(test)
        .unsafeRunSync()
    }
}
