package kirill5k.cryptotracker

import cats.effect.{ContextShift, IO, Timer}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.ExecutionContext

trait CatsIOSpec extends AsyncWordSpec with Matchers {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val logger: Logger[IO]   = Slf4jLogger.getLogger[IO]
  implicit val timer: Timer[IO]     = IO.timer(ExecutionContext.global)
}