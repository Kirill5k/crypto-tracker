package kirill5k.cryptotracker.tasks

import cats.Parallel
import cats.effect.Concurrent
import cats.implicits._
import fs2.Stream
import kirill5k.cryptotracker.common.config.AppConfig
import kirill5k.cryptotracker.services.Services

final case class Tasks[F[_]: Concurrent](
    tasks: List[Task[F]]
) {

  def runAll(): Stream[F, Unit] =
    Stream
      .emits(tasks.map(_.run()))
      .parJoinUnbounded
}

object Tasks {
  def make[F[_]: Parallel: Concurrent](config: AppConfig, services: Services[F]): F[Tasks[F]] =
    List(
      MentionsFinder.reddit[F](config.reddit, services.mention)
    ).parSequence.map(Tasks[F])
}
