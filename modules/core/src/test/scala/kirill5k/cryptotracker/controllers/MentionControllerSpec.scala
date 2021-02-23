package kirill5k.cryptotracker.controllers

import cats.effect.IO
import kirill5k.cryptotracker.ControllerSpec
import kirill5k.cryptotracker.domain.Ticker
import kirill5k.cryptotracker.domain.MentionBuilder._
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
        verifyZeroInteractions(service)
      }
    }

    "GET /mentions/summary" should {
      val dateFrom = Instant.parse("2021-01-01T00:00:00Z")
      val dateTo = Instant.parse("2021-01-01T00:30:00Z")
      val mentions = List(
        mention(Ticker("BB"), time = dateFrom.plusSeconds(180)),
        mention(Ticker("BB"), time = dateFrom.plusSeconds(360)),
        mention(Ticker("BB"), time = dateFrom.plusSeconds(540)),
        mention(Ticker("AAPL"), time = dateFrom.plusSeconds(720)),
        mention(Ticker("AAPL"), time = dateFrom.plusSeconds(900))
      )

      "return summary of mentions during specified date period" in {
        val service = mock[MentionService[IO]]
        when(service.findAll(any[Instant], any[Instant])).thenReturn(IO.pure(mentions))

        val controller = new MentionController[IO](service)
        val request  = Request[IO](uri = uri"/mentions/summary?from=2021-01-01T00:00:00Z&to=2021-01-01T00:30:00Z")
        val response = controller.routes.orNotFound.run(request)

        val expectedResponse =
          s"""{
             |"dateRange": {"from":"2021-01-01T00:00:00Z","to":"2021-01-01T00:30:00Z"},
             |"summaries": [
             |{"ticker":"AAPL","total":2,"times":["2021-01-01T00:12:00Z","2021-01-01T00:15:00Z"]},
             |{"ticker":"BB","total":3,"times":["2021-01-01T00:03:00Z","2021-01-01T00:06:00Z","2021-01-01T00:09:00Z"]}
             |]
             |}""".stripMargin
        verifyJsonResponse(response, Status.Ok, Some(expectedResponse))
        verify(service).findAll(dateFrom, dateTo)
      }
    }

    "GET /mentions/:ticker" should {

      "parse ticker from path" in {
        val service = mock[MentionService[IO]]
        when(service.findBy(any[Ticker], any[Option[Instant]], any[Option[Instant]])).thenReturn(IO.pure(Nil))

        val controller = new MentionController[IO](service)
        val request  = Request[IO](uri = uri"/mentions/BB", method = Method.GET)
        val response = controller.routes.orNotFound.run(request)

        verifyJsonResponse(response, Status.Ok, Some("""[]"""))
        verify(service).findBy(Ticker("BB"), None, None)
      }
    }
  }
}
