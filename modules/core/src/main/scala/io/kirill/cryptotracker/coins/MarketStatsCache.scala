package io.kirill.cryptotracker.coins

import java.time.Instant

import cats.effect.{Concurrent, Sync, Timer}
import cats.effect.concurrent.Ref
import cats.implicits._

import scala.concurrent.duration._
import scala.language.postfixOps

sealed trait MarketStatsCache[F[_]] {
  def add(stats: MarketStats): F[Unit]
  def get(coin: Cryptocoin): F[List[MarketStats]]
}

final private class RefMarketStatsCache[F[_]: Sync](
    val cache: Ref[F, Map[Cryptocoin, List[MarketStats]]]
) extends MarketStatsCache[F] {
  override def add(stats: MarketStats): F[Unit] =
    cache.update(h => h + (stats.coin -> (stats +: h.getOrElse(stats.coin, Nil))))

  override def get(coin: Cryptocoin): F[List[MarketStats]] =
    cache.get.map(_.getOrElse(coin, Nil))
}

object MarketStatsCache {

  def refMarketStatsCache[F[_]: Concurrent: Timer](
      expiresIn: FiniteDuration,
      checkOnExpirationsEvery: FiniteDuration = 15 minutes
  ): F[MarketStatsCache[F]] = {
    def runExpiration(cache: Ref[F, Map[Cryptocoin, List[MarketStats]]]): F[Unit] = {
      val process = cache.get
        .map(_.map {
          case (c, stats) => (c, stats.filter(s => s.timestamp.isAfter(Instant.now().minusSeconds(expiresIn.toSeconds))))
        })
        .flatTap(cache.set)
      Timer[F].sleep(checkOnExpirationsEvery) >> process >> runExpiration(cache)
    }
    Ref
      .of[F, Map[Cryptocoin, List[MarketStats]]](Map.empty)
      .flatTap(s => Concurrent[F].start(runExpiration(s)).void)
      .map(ref => new RefMarketStatsCache[F](ref))
  }
}
