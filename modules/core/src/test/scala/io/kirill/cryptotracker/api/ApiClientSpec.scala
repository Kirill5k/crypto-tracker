package io.kirill.cryptotracker.api

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{ContextShift, IO}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.concurrent.ExecutionContext
import scala.io.Source

trait ApiClientSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.Implicits.global)
  implicit val logger: Logger[IO]   = Slf4jLogger.getLogger[IO]

  def json(path: String): String = Source.fromResource(path).getLines.toList.mkString
}
