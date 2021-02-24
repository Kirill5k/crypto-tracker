package kirill5k.cryptotracker.common

import cats.effect.Timer
import fs2.Stream

import scala.concurrent.duration._

object Streams {

  def fixedRateImmediate[F[_]](d: FiniteDuration)(implicit timer: Timer[F]): Stream[F, Unit] = {
    def now: Stream[F, Long] = Stream.eval(timer.clock.monotonic(NANOSECONDS))
    def go(started: Long): Stream[F, Unit] =
      now.flatMap { finished =>
        val elapsed = finished - started
        Stream.emit(()) ++ Stream.sleep_(d - elapsed.nanos) ++ now.flatMap { started =>
          go(started)
        }
      }
    now.flatMap(go)
  }
}
