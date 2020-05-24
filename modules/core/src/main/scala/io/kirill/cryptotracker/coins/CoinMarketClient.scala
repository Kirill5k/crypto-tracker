package io.kirill.cryptotracker.coins

import java.time.Instant

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.kirill.cryptotracker.api.coinlib.CoinlibApi
import io.kirill.cryptotracker.config.AppConfig
import squants.market.GBP
import sttp.client.{NothingT, SttpBackend}

sealed trait CoinMarketClient[F[_]] {
  def liveStats(coin: Cryptocoin): F[MarketStats]
}

final private class CoinlibClient[F[_]: Sync: Logger](
    implicit val backend: SttpBackend[F, Nothing, NothingT],
    val config: AppConfig
) extends CoinMarketClient[F] {

  override def liveStats(coin: Cryptocoin): F[MarketStats] =
    CoinlibApi.coin[F](coin.symbol.value).map { r =>
      MarketStats(
        coin,
        CoinPrice(GBP(r.price), r.delta_1h, r.delta_24h, r.delta_7d, r.delta_30d),
        MarketVolume(r.total_volume_24h),
        Instant.ofEpochSecond(r.last_updated_timestamp)
      )
    }
}

object CoinMarketClient {
  def make[F[_]: Sync: Logger](
      implicit backend: SttpBackend[F, Nothing, NothingT],
      config: AppConfig
  ): F[CoinMarketClient[F]] =
    Sync[F].delay(new CoinlibClient[F])
}
