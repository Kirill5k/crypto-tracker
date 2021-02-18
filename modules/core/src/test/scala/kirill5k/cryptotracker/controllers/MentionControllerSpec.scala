package kirill5k.cryptotracker.controllers

import cats.effect.IO
import kirill5k.cryptotracker.ControllerSpec
import kirill5k.cryptotracker.domain.Ticker
import kirill5k.cryptotracker.services.MentionService
import org.http4s._
import org.http4s.implicits._

import java.time.Instant

class MentionControllerSpec extends ControllerSpec {

  "A MentionController" when {

    "GET /mentions" should {

      "return 400 when date params are missing" in {
        val service = mock[MentionService[IO]]
        val controller = new MentionController[IO](service)

        val request  = Request[IO](uri = uri"/mentions", method = Method.GET)
        val response = controller.routes.orNotFound.run(request)

        verifyJsonResponse(response, Status.BadRequest, Some("""{"message": "query parameter 'from' is required"}"""))
      }
    }

    "GET /mentions/:ticker" should {

      "parse ticker from path" in {
        val service = mock[MentionService[IO]]
        val controller = new MentionController[IO](service)

        when(service.findBy(any[Ticker], any[Option[Instant]], any[Option[Instant]])).thenReturn(IO.pure(Nil))

        val request  = Request[IO](uri = uri"/mentions/BB", method = Method.GET)
        val response = controller.routes.orNotFound.run(request)

        verifyJsonResponse(response, Status.Ok, Some("""[]"""))
        verify(service).findBy(Ticker("BB"), None, None)
      }
    }
  }
}
