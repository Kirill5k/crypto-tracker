package io.kirill.cryptotracker.coins

import java.time.Instant

import cats.effect.IO
import cats.implicits._
import io.kirill.cryptotracker.CatsIOSpec
import squants.market.{GBP, Money}

import scala.concurrent.duration._
import scala.language.postfixOps

class CoinStatsCacheSpec extends CatsIOSpec {
  import Builder._

  "A RefCoinStatsCache" - {
    "should store stats in cache" in {
      val stats = List(
        coinStats(timestamp = Instant.now().minusSeconds(1000)),
        coinStats(timestamp = Instant.now().minusSeconds(100)),
        coinStats(timestamp = Instant.now().minusSeconds(10))
      )

      val result = for {
        cache <- CoinStatsCache.refCoinStatsCache[IO](2 hours, 5 minutes)
        _     <- stats.map(s => cache.add(s)).sequence
        res   <- cache.get(Bitcoin)
      } yield res

      result.asserting(_ must contain theSameElementsInOrderAs stats.reverse)
    }

    "should return empty list if no stats exist" in {
      val result = for {
        cache <- CoinStatsCache.refCoinStatsCache[IO](2 hours, 5 minutes)
        res   <- cache.get(Bitcoin)
      } yield res

      result.asserting(_ must be (Nil))
    }

    "should clear expired stats" in {
      val result = for {
        cache <- CoinStatsCache.refCoinStatsCache[IO](2 seconds, 100 millis)
        _ <- cache.add(coinStats(timestamp = Instant.now()))
        < <- IO.sleep(3 seconds)
        res   <- cache.get(Bitcoin)
      } yield res

      result.asserting(_ must be (Nil))
    }
  }
}
