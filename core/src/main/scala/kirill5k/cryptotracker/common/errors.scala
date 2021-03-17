package kirill5k.cryptotracker.common

import kirill5k.cryptotracker.domain.Ticker

object errors {
  sealed trait AppError extends Throwable {
    val message: String
    override def getMessage: String = message
  }

  object AppError {
    final case class CompanyNotFound(ticker: Ticker) extends AppError {
      override val message: String = s"company ${ticker.value} cannot be found"
    }

    final case class MissingQueryParam(name: String)     extends AppError {
      override val message: String = s"query parameter '$name' is required"
    }

    final case class Http(statusCode: Int, message: String) extends AppError
    final case class Json(message: String)                  extends AppError
  }
}
