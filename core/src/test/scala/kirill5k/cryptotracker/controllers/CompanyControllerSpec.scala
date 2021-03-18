package kirill5k.cryptotracker.controllers

import cats.effect.IO
import kirill5k.cryptotracker.ControllerSpec
import kirill5k.cryptotracker.common.errors.AppError.CompanyNotFound
import kirill5k.cryptotracker.domain.{CompanyBuilder, Ticker}
import kirill5k.cryptotracker.services.CompanyService
import org.http4s._
import org.http4s.implicits._

class CompanyControllerSpec extends ControllerSpec {

  "A CompanyController" should {

    "GET /companies/:ticker" should {

      "return company by ticker" in {
        val service = mock[CompanyService[IO]]
        val company = CompanyBuilder.company(Ticker("BB"))
        when(service.find(any[Ticker])).thenReturn(IO.pure(company))

        val controller = new CompanyController[IO](service)
        val request  = Request[IO](uri = uri"/companies/BB", method = Method.GET)
        val response = controller.routes.orNotFound.run(request)

        verifyJsonResponse(response, Status.Ok, Some("""{"ticker":"BB","name":"Company","industry":"Industry","region":"Region"}"""))
        verify(service).find(Ticker("BB"))
      }

      "return 404 when company cannot be found" in {
        val service = mock[CompanyService[IO]]
        when(service.find(any[Ticker])).thenReturn(IO.raiseError(CompanyNotFound(Ticker("BB"))))

        val controller = new CompanyController[IO](service)
        val request  = Request[IO](uri = uri"/companies/BB", method = Method.GET)
        val response = controller.routes.orNotFound.run(request)

        verifyJsonResponse(response, Status.NotFound, Some("""{"message":"company BB cannot be found"}"""))
        verify(service).find(Ticker("BB"))
      }
    }
  }
}
