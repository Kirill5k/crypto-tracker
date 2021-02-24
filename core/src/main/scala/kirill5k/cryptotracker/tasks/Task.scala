package kirill5k.cryptotracker.tasks

import fs2.Stream

trait Task[F[_]] {
  def run(): Stream[F, Unit]
}
