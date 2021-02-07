package kirill5k.cryptotracker.common

object errors {
  sealed trait AppError extends Throwable {
    val message: String
    override def getMessage: String = message
  }

  object AppError {
    final case class Http(statusCode: Int, message: String) extends AppError
  }
}
