package io.kirill.cryptotracker

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{ContextShift, IO}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.kirill.cryptotracker.config.AppConfig
import org.mockito.ArgumentMatchersSugar
import org.mockito.scalatest.AsyncMockitoSugar
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.concurrent.ExecutionContext
import scala.io.Source

trait CatsIOSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  implicit val ac: AppConfig        = AppConfig.load
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.Implicits.global)
  implicit val logger: Logger[IO]   = Slf4jLogger.getLogger[IO]
}
