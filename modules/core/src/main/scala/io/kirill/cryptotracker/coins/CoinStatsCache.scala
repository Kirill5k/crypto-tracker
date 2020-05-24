package io.kirill.cryptotracker.coins

import java.time.Instant

import cats.effect.{Concurrent, Sync, Timer}
import cats.effect.concurrent.Ref
import cats.implicits._

import scala.concurrent.duration.FiniteDuration

sealed trait CoinStatsCache[F[_]] {
  def add(stats: CoinStats): F[Unit]
  def get(coin: Cryptocoin): F[List[CoinStats]]
}

final private class RefCoinStatsCache[F[_]: Sync](
    val cache: Ref[F, Map[Cryptocoin, List[CoinStats]]]
) extends CoinStatsCache[F] {
  override def add(stats: CoinStats): F[Unit] =
    cache.update(h => h + (stats.coin -> (stats +: h.getOrElse(stats.coin, Nil))))

  override def get(coin: Cryptocoin): F[List[CoinStats]] =
    cache.get.map(_.getOrElse(coin, Nil))
}

object CoinStatsCache {

  def refCoinStatsCache[F[_]: Concurrent: Timer](
      expiresIn: FiniteDuration,
      checkOnExpirationsEvery: FiniteDuration
  ): F[CoinStatsCache[F]] = {
    def runExpiration(cache: Ref[F, Map[Cryptocoin, List[CoinStats]]]): F[Unit] = {
      val process = cache.get.map(_.map {
        case (c, stats) => (c, stats.filter(s => s.timestamp.isAfter(Instant.now().minusSeconds(expiresIn.toSeconds))))
      }).flatTap(cache.set)
      Timer[F].sleep(checkOnExpirationsEvery) >> process >> runExpiration(cache)
    }
    Ref.of[F, Map[Cryptocoin, List[CoinStats]]](Map.empty)
      .flatTap(s => Concurrent[F].start(runExpiration(s)).void)
      .map(ref => new RefCoinStatsCache[F](ref))
  }
}
