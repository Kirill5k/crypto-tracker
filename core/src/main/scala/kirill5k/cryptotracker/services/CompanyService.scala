package kirill5k.cryptotracker.services

import cats.Monad
import cats.implicits._
import kirill5k.cryptotracker.clients.alphavantage.AlphaVantageClient
import kirill5k.cryptotracker.domain.{Company, Ticker}
import kirill5k.cryptotracker.repositories.CompanyRepository

trait CompanyService[F[_]] {
  def find(ticker: Ticker): F[Company]
}

private final class LiveCompanyService[F[_]: Monad](
    private val alphaVantageClient: AlphaVantageClient[F],
    private val companyRepository: CompanyRepository[F]
) extends CompanyService[F] {
  override def find(ticker: Ticker): F[Company] =
    companyRepository.existsBy(ticker).flatMap {
      case true => companyRepository.findBy(ticker)
      case false => alphaVantageClient.findCompany(ticker).map(_.get)
    }
}
