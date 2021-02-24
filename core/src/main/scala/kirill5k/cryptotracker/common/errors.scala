package kirill5k.cryptotracker.common

object errors {
  sealed trait AppError extends Throwable {
    val message: String
    override def getMessage: String = message
  }

  object AppError {
    final case class MissingQueryParam(name: String)     extends AppError {
      override val message: String = s"query parameter '$name' is required"
    }

    final case class Http(statusCode: Int, message: String) extends AppError
    final case class Json(message: String)                  extends AppError
  }
}
