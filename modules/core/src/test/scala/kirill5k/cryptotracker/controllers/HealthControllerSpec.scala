package kirill5k.cryptotracker.controllers

import cats.effect.IO
import kirill5k.cryptotracker.ControllerSpec
import org.http4s._
import org.http4s.implicits._

class HealthControllerSpec extends ControllerSpec {

  "A HealthController" should {

    "return 200 response" in {
      val controller = new HealthController[IO]

      val request  = Request[IO](uri = uri"/health/status", method = Method.GET)
      val response = controller.routes.orNotFound.run(request)

      verifyJsonResponse(response, Status.Ok, Some("""{"status": true}"""))
    }
  }
}
