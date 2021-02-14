package kirill5k.cryptotracker

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import kirill5k.cryptotracker.clients.Clients
import kirill5k.cryptotracker.common.Resources
import kirill5k.cryptotracker.common.config.AppConfig
import kirill5k.cryptotracker.services.Services
import kirill5k.cryptotracker.tasks.Tasks

object Main extends IOApp {

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _      <- logger.info("starting crypto tracker")
      config <- Blocker[IO].use(AppConfig.load[IO]) <* logger.info("loaded config")
      _ <- Resources.make[IO].use { resources =>
        for {
          clients  <- Clients.make[IO](config, resources.sttpBackend)
          services <- Services.make[IO](clients)
          tasks    <- Tasks.make[IO](config, services)
          _        <- tasks.runAll().compile.drain <* logger.info("started all tasks")
        } yield ()
      }
    } yield ExitCode.Success
}
