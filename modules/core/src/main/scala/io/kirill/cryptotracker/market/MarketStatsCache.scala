package io.kirill.cryptotracker.market

import java.time.Instant

import cats.effect.{Concurrent, Sync, Timer}
import cats.effect.concurrent.Ref
import cats.implicits._
import io.kirill.cryptotracker.market.coins.Cryptocoin
import io.kirill.cryptotracker.market.indicators.MarketIndicators

import scala.concurrent.duration._
import scala.language.postfixOps

sealed trait MarketStatsCache[F[_]] {
  def add(indicators: MarketIndicators): F[Unit]
  def get(coin: Cryptocoin): F[List[MarketIndicators]]
}

final private class RefMarketStatsCache[F[_]: Sync](
    val cache: Ref[F, Map[Cryptocoin, List[MarketIndicators]]]
) extends MarketStatsCache[F] {
  override def add(indicators: MarketIndicators): F[Unit] =
    cache.update(h => h + (indicators.coin -> (indicators +: h.getOrElse(indicators.coin, Nil))))

  override def get(coin: Cryptocoin): F[List[MarketIndicators]] =
    cache.get.map(_.getOrElse(coin, Nil))
}

object MarketStatsCache {

  def refMarketStatsCache[F[_]: Concurrent: Timer](
      expiresIn: FiniteDuration,
      checkOnExpirationsEvery: FiniteDuration = 15 minutes
  ): F[MarketStatsCache[F]] = {
    def runExpiration(cache: Ref[F, Map[Cryptocoin, List[MarketIndicators]]]): F[Unit] = {
      val process = cache.get
        .map(_.map {
          case (c, indicators) => (c, indicators.filter(s => s.timestamp.isAfter(Instant.now().minusSeconds(expiresIn.toSeconds))))
        })
        .flatTap(cache.set)
      Timer[F].sleep(checkOnExpirationsEvery) >> process >> runExpiration(cache)
    }
    Ref
      .of[F, Map[Cryptocoin, List[MarketIndicators]]](Map.empty)
      .flatTap(s => Concurrent[F].start(runExpiration(s)).void)
      .map(ref => new RefMarketStatsCache[F](ref))
  }
}
