package io.kirill.cryptotracker

import cats.effect.{ExitCode, IO, IOApp}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.kirill.cryptotracker.config.AppConfig

object Main extends IOApp {
  implicit val ac: AppConfig      = AppConfig.load
  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- logger.info(s"Hello, World!")
    } yield ExitCode.Success
}
