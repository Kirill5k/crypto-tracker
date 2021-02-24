package kirill5k.cryptotracker.tasks

import cats.Parallel
import cats.effect.{Concurrent, Timer}
import cats.implicits._
import fs2.Stream
import io.chrisdavenport.log4cats.Logger
import kirill5k.cryptotracker.common.config.AppConfig
import kirill5k.cryptotracker.services.Services

import scala.concurrent.duration._

final case class Tasks[F[_]: Concurrent: Logger: Timer](
    tasks: List[Task[F]]
) {

  def runAll(): Stream[F, Unit] =
    Stream
      .emits(tasks.map(_.run().resumeOnError(1.minute)))
      .parJoinUnbounded

  implicit final private class StreamOps[O](private val stream: Stream[F, O]) {
    def resumeOnError(delay: FiniteDuration)(implicit logger: Logger[F], timer: Timer[F]): Stream[F, O] =
      stream.handleErrorWith { error =>
        Stream.eval_(logger.error(error)("error during task processing")) ++
          stream.delayBy(delay)(timer)
      }
  }
}

object Tasks {
  def make[F[_]: Parallel: Concurrent: Logger: Timer](config: AppConfig, services: Services[F]): F[Tasks[F]] =
    List(
      RedditMentionsFinder.make[F](config.reddit, services.mention)
    ).parSequence.map(Tasks[F])
}
