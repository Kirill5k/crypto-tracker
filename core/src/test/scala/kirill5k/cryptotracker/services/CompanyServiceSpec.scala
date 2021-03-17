package kirill5k.cryptotracker.services

import cats.effect.IO
import kirill5k.cryptotracker.CatsIOSpec
import kirill5k.cryptotracker.clients.alphavantage.AlphaVantageClient
import kirill5k.cryptotracker.common.errors.AppError.CompanyNotFound
import kirill5k.cryptotracker.domain.{Company, CompanyBuilder, Ticker}
import kirill5k.cryptotracker.repositories.CompanyRepository

class CompanyServiceSpec extends CatsIOSpec {

  "A CompanyService" should {

    val company = CompanyBuilder.company(Ticker("BB"))

    "return company from repo if it exists" in {
      val (repo, client) = mocks
      when(repo.findBy(any[Ticker])).thenReturn(IO.pure(Some(company)))

      val res = CompanyService
        .make[IO](repo, client)
        .flatMap(_.find(Ticker("BB")))

      res.unsafeToFuture().map { c =>
        verify(repo).findBy(Ticker("BB"))
        verifyZeroInteractions(client)
        c mustBe company
      }
    }

    "find company through alpha vantage if it is new and save it in db" in {
      val (repo, client) = mocks
      when(repo.findBy(any[Ticker])).thenReturn(IO.pure(None))
      when(client.findCompany(any[Ticker])).thenReturn(IO.pure(Some(company)))
      when(repo.save(any[Company])).thenReturn(IO.unit)

      val res = CompanyService
        .make[IO](repo, client)
        .flatMap(_.find(Ticker("BB")))

      res.unsafeToFuture().map { c =>
        verify(repo).findBy(Ticker("BB"))
        verify(client).findCompany(Ticker("BB"))
        verify(repo).save(company)
        c mustBe company
      }
    }

    "return error if company cannot be found" in {
      val (repo, client) = mocks
      when(repo.findBy(any[Ticker])).thenReturn(IO.pure(None))
      when(client.findCompany(any[Ticker])).thenReturn(IO.pure(None))

      val res = CompanyService
        .make[IO](repo, client)
        .flatMap(_.find(Ticker("BB")))

      res.attempt.unsafeToFuture().map { c =>
        verify(repo).findBy(Ticker("BB"))
        verify(client).findCompany(Ticker("BB"))
        verify(repo, never).save(any[Company])
        c mustBe Left(CompanyNotFound(Ticker("BB")))
      }
    }
  }

  def mocks: (CompanyRepository[IO], AlphaVantageClient[IO]) = {
    val repo = mock[CompanyRepository[IO]]
    val client = mock[AlphaVantageClient[IO]]
    (repo, client)
  }
}
