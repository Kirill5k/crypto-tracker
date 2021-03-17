package kirill5k.cryptotracker.services

import cats.MonadError
import cats.effect.Sync
import cats.implicits._
import kirill5k.cryptotracker.clients.alphavantage.AlphaVantageClient
import kirill5k.cryptotracker.common.errors.AppError.CompanyNotFound
import kirill5k.cryptotracker.domain.{Company, Ticker}
import kirill5k.cryptotracker.repositories.CompanyRepository

trait CompanyService[F[_]] {
  def find(ticker: Ticker): F[Company]
}

final private class LiveCompanyService[F[_]](
    private val companyRepository: CompanyRepository[F],
    private val alphaVantageClient: AlphaVantageClient[F]
)(implicit F: MonadError[F, Throwable])
    extends CompanyService[F] {
  override def find(ticker: Ticker): F[Company] =
    companyRepository.findBy(ticker).flatMap {
      case Some(company) => company.pure[F]
      case None          => searchForCompany(ticker)
    }

  private def searchForCompany(ticker: Ticker): F[Company] =
    alphaVantageClient.findCompany(ticker).flatMap {
      case Some(company) => companyRepository.save(company) *> company.pure[F]
      case None          => CompanyNotFound(ticker).raiseError[F, Company]
    }
}

object CompanyService {
  def make[F[_]: Sync](repository: CompanyRepository[F], client: AlphaVantageClient[F]): F[CompanyService[F]] =
    Sync[F].delay(new LiveCompanyService[F](repository, client))
}
