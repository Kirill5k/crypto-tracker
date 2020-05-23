package io.kirill.cryptotracker

object errors {
  sealed trait AppError extends Throwable {
    val message: String
    override def getMessage: String = message
  }

  final case class ApiClientError(statusCode: Int, message: String) extends AppError
}
