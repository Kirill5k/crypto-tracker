package kirill5k.cryptotracker.controllers

import cats.effect.IO
import kirill5k.cryptotracker.ControllerSpec
import kirill5k.cryptotracker.domain.{CompanyBuilder, Ticker}
import kirill5k.cryptotracker.services.{CompanyService, MentionService, Services}
import org.http4s._
import org.http4s.implicits._

class ControllersSpec extends ControllerSpec {

  "A Controllers module" should {

    "serve health routes" in {
      val svc = services

      val request  = Request[IO](uri = uri"/health/status", method = Method.GET)
      val response = Controllers.make[IO](svc).flatMap(_.routes.orNotFound.run(request))

      verifyJsonResponse(response, Status.Ok, Some("""{"status": true}"""))
    }

    "serve mention resource routes" in {
      val svc = services
      when(svc.mention.findBy(any[Ticker], eqTo(None), eqTo(None))).thenReturn(IO.pure(Nil))

      val request  = Request[IO](uri = uri"/api/mentions/BB", method = Method.GET)
      val response = Controllers.make[IO](svc).flatMap(_.routes.orNotFound.run(request))

      verifyJsonResponse(response, Status.Ok, Some("""[]"""))
      verify(svc.mention).findBy(Ticker("BB"), None, None)
    }

    "serve company resource routes" in {
      val svc = services
      when(svc.company.find(any[Ticker])).thenReturn(IO.pure(CompanyBuilder.company(Ticker("BB"))))

      val request  = Request[IO](uri = uri"/api/companies/BB", method = Method.GET)
      val response = Controllers.make[IO](svc).flatMap(_.routes.orNotFound.run(request))

      verifyJsonResponse(response, Status.Ok, Some("""{"ticker":"BB","name":"Company","industry":"Industry","region":"Region"}"""))
      verify(svc.company).find(Ticker("BB"))
    }
  }

  def services: Services[IO] = Services[IO](
    mock[MentionService[IO]],
    mock[CompanyService[IO]]
  )
}
