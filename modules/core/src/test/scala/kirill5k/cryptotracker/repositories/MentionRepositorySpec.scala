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

  "MentionRepository" should {

    "save mentions in database" in {
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

  def withEmbeddedMongoClient[A](test: MongoClientF[IO] => IO[A]): A =
    withRunningEmbeddedMongo() {
      MongoClientF
        .fromConnectionString[IO]("mongodb://localhost:12345")
        .use(test)
        .unsafeRunSync()
    }
}
