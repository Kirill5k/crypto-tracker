package io.kirill.cryptotracker.coins

import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.kirill.cryptotracker.api.coinlib.CoinlibApi
import io.kirill.cryptotracker.config.AppConfig
import squants.market.GBP
import sttp.client.{NothingT, SttpBackend}

sealed trait CoinPriceClient[F[_]] {
  def getCurrentPrice(coin: Cryptocoin): F[CoinPrice]
}

final private class CoinlibPriceClient[F[_]: Sync: Logger](
    implicit val backend: SttpBackend[F, Nothing, NothingT],
    val config: AppConfig
) extends CoinPriceClient[F] {

  override def getCurrentPrice(coin: Cryptocoin): F[CoinPrice] =
    CoinlibApi.coin[F](coin.symbol.value).map { r =>
      CoinPrice(GBP(r.price), r.delta_1h, r.delta_24h, r.delta_7d, r.delta_30d)
    }
}

object CoinPriceClient {
  def make[F[_]: Sync: Logger](
      implicit backend: SttpBackend[F, Nothing, NothingT],
      config: AppConfig
  ): F[CoinPriceClient[F]] =
    Sync[F].delay(new CoinlibPriceClient[F])
}
