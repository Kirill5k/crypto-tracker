package io.kirill.cryptotracker.coins

import cats.effect.IO
import io.kirill.cryptotracker.CatsIOSpec
import org.mockito.ArgumentMatchersSugar
import org.mockito.scalatest.AsyncMockitoSugar

class CoinServiceSpec extends CatsIOSpec with AsyncMockitoSugar with ArgumentMatchersSugar {
  import Builder._

  "A LiveCoinService" - {

    "should current coin stats" in {
      val stats           = marketStats()
      val (client, cache) = mocks
      val result = for {
        service <- CoinService.make[IO](client, cache)
        _ = when(client.liveStats(any[Cryptocoin])).thenReturn(IO.pure(stats))
        _ = when(cache.add(any[MarketStats])).thenReturn(IO.unit)
        res <- service.currentStats(Bitcoin)
      } yield res

      result.asserting { s =>
        verify(client).liveStats(Bitcoin)
        verify(cache).add(s)
        s must be(stats)
      }
    }
  }

  def mocks: (CoinMarketClient[IO], MarketStatsCache[IO]) =
    (mock[CoinMarketClient[IO]], mock[MarketStatsCache[IO]])
}
