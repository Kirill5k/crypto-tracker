package io.kirill.cryptotracker.market

import java.time.Instant

import cats.effect.IO
import cats.implicits._
import io.kirill.cryptotracker.CatsIOSpec
import io.kirill.cryptotracker.market.coins.Bitcoin

import scala.concurrent.duration._
import scala.language.postfixOps

class MarketStatsCacheSpec extends CatsIOSpec {
  import Builder._

  "A RefMarketStatsCache" - {
    "should store stats in cache" in {
      val stats = List(
        marketStats(timestamp = Instant.now().minusSeconds(1000)),
        marketStats(timestamp = Instant.now().minusSeconds(100)),
        marketStats(timestamp = Instant.now().minusSeconds(10))
      )

      val result = for {
        cache <- MarketStatsCache.refMarketStatsCache[IO](2 hours, 5 minutes)
        _     <- stats.map(s => cache.add(s)).sequence
        res   <- cache.get(Bitcoin)
      } yield res

      result.asserting(_ must contain theSameElementsInOrderAs stats.reverse)
    }

    "should return empty list if no stats exist" in {
      val result = for {
        cache <- MarketStatsCache.refMarketStatsCache[IO](2 hours, 5 minutes)
        res   <- cache.get(Bitcoin)
      } yield res

      result.asserting(_ must be(Nil))
    }

    "should clear expired stats" in {
      val result = for {
        cache <- MarketStatsCache.refMarketStatsCache[IO](2 seconds, 100 millis)
        _     <- cache.add(marketStats(timestamp = Instant.now()))
        <     <- IO.sleep(3 seconds)
        res   <- cache.get(Bitcoin)
      } yield res

      result.asserting(_ must be(Nil))
    }
  }
}
