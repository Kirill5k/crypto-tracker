package io.kirill.cryptotracker.coins

import cats.effect.IO
import io.kirill.cryptotracker.CatsIOSpec

class CoinServiceSpec extends CatsIOSpec {
  import Builder._

  "A LiveCoinService" - {

    "should current coin stats" in {
      val (client, cache) = mocks
      val result = for {
        service <- CoinService.make[IO](client, cache)
        _ = when(client.getCurrentPrice(any[Cryptocoin])).thenReturn(IO.pure(coinPrice()))
        _ = when(cache.add(any[CoinStats])).thenReturn(IO.unit)
        res <- service.currentStats(Bitcoin)
      } yield res

      result.asserting { stats =>
        verify(client).getCurrentPrice(Bitcoin)
        verify(cache).add(any[CoinStats])
        stats.coin must be (Bitcoin)
        stats.price must be (coinPrice())
      }
    }
  }

  def mocks: (CoinPriceClient[IO], CoinStatsCache[IO]) =
    (mock[CoinPriceClient[IO]], mock[CoinStatsCache[IO]])
}
