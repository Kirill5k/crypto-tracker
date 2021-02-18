package kirill5k.cryptotracker.controllers

import cats.effect.IO
import kirill5k.cryptotracker.ControllerSpec
import kirill5k.cryptotracker.services.MentionService
import org.http4s._
import org.http4s.implicits._

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

      "return 400 when date params are missing" in {
        val service = mock[MentionService[IO]]
        val controller = new MentionController[IO](service)

        val request  = Request[IO](uri = uri"/mentions/BB", method = Method.GET)
        val response = controller.routes.orNotFound.run(request)

        verifyJsonResponse(response, Status.BadRequest, Some("""{"message": "query parameter 'from' is required"}"""))
      }
    }
  }
}
