package kirill5k.cryptotracker

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import kirill5k.cryptotracker.clients.Clients
import kirill5k.cryptotracker.common.Resources
import kirill5k.cryptotracker.common.config.AppConfig
import kirill5k.cryptotracker.controllers.Controllers
import kirill5k.cryptotracker.repositories.Repositories
import kirill5k.cryptotracker.services.Services
import kirill5k.cryptotracker.tasks.Tasks
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

import scala.concurrent.ExecutionContext

object Main extends IOApp {

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _      <- logger.info("starting crypto tracker")
      config <- Blocker[IO].use(AppConfig.load[IO]) <* logger.info("loaded config")
      _ <- Resources.make[IO](config).use { resources =>
        for {
          clients        <- Clients.make[IO](config, resources.sttpBackend)
          repositories   <- Repositories.make[IO](resources.mongoClient)
          services       <- Services.make[IO](clients, repositories)
          tasks          <- Tasks.make[IO](config, services)
          tasksProcesses <- tasks.runAll().compile.drain.start <* logger.info("started all tasks")
          controllers    <- Controllers.make[IO]
          _ <- BlazeServerBuilder[IO](ExecutionContext.global)
            .bindHttp(config.server.port, config.server.host)
            .withHttpApp(controllers.routes.orNotFound)
            .serve
            .compile
            .drain
          _ <- logger.info("shutting down crypto tracker") *> tasksProcesses.cancel
        } yield ()
      }
    } yield ExitCode.Success
}
