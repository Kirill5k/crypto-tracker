package kirill5k.cryptotracker.clients.alphavantage

import cats.effect.IO
import kirill5k.cryptotracker.RequestOps._
import kirill5k.cryptotracker.SttpClientSpec
import kirill5k.cryptotracker.common.config.AlphaVantageConfig
import kirill5k.cryptotracker.domain.{Company, Ticker, TimeSeries}
import sttp.client3.{Response, SttpBackend}
import sttp.model.StatusCode

class AlphaVantageClientSpec extends SttpClientSpec {

  val config = AlphaVantageConfig("http://alphavantage.com", "av-api-key")

  "A AlphaVantageClient" when {

    "findCompany" should {
      "find company information by ticker" in {
        val searchEndpoint = "query"
        val params         = Map("function" -> "SYMBOL_SEARCH", "keywords" -> "PLTR", "apikey" -> "av-api-key")
        val testingBackend: SttpBackend[IO, Any] = backendStub
          .whenRequestMatchesPartial {
            case r if r.isGet && r.isGoingTo(s"alphavantage.com/$searchEndpoint") && r.hasParams(params) =>
              Response.ok(json("alpha-vantage/symbol-search-response.json"))
            case r => throw new RuntimeException(r.uri.toString())
          }

        val telegramClient = AlphaVantageClient.make[IO](config, testingBackend)
        val result         = telegramClient.flatMap(_.findCompany(Ticker("PLTR")))

        result.unsafeToFuture().map { company =>
          company mustBe Some(Company(Ticker("PLTR"), "Palantir Technologies Inc - Class A", "Equity", "United States"))
        }
      }

      "return empty option when company not found" in {
        val searchEndpoint = "query"
        val params         = Map("function" -> "SYMBOL_SEARCH", "keywords" -> "BB", "apikey" -> "av-api-key")
        val testingBackend: SttpBackend[IO, Any] = backendStub
          .whenRequestMatchesPartial {
            case r if r.isGet && r.isGoingTo(s"alphavantage.com/$searchEndpoint") && r.hasParams(params) =>
              Response.ok(json("alpha-vantage/symbol-search-response.json"))
            case r => throw new RuntimeException(r.uri.toString())
          }

        val telegramClient = AlphaVantageClient.make[IO](config, testingBackend)
        val result         = telegramClient.flatMap(_.findCompany(Ticker("BB")))

        result.unsafeToFuture().map { company =>
          company mustBe None
        }
      }

      "return empty option in case of errors" in {
        val searchEndpoint = "query"
        val params         = Map("function" -> "SYMBOL_SEARCH", "keywords" -> "BB", "apikey" -> "av-api-key")
        val testingBackend: SttpBackend[IO, Any] = backendStub
          .whenRequestMatchesPartial {
            case r if r.isGet && r.isGoingTo(s"alphavantage.com/$searchEndpoint") && r.hasParams(params) =>
              Response("oups", StatusCode.BadRequest)
            case r => throw new RuntimeException(r.uri.toString())
          }

        val telegramClient = AlphaVantageClient.make[IO](config, testingBackend)
        val result         = telegramClient.flatMap(_.findCompany(Ticker("BB")))

        result.unsafeToFuture().map { company =>
          company mustBe None
        }
      }
    }

    "getWeeklyPrices" should {

      "should return weekly prices for a given ticker" in {
        val searchEndpoint = "query"
        val params         = Map("function" -> "TIME_SERIES_WEEKLY", "symbol" -> "PLTR", "apikey" -> "av-api-key")
        val testingBackend: SttpBackend[IO, Any] = backendStub
          .whenRequestMatchesPartial {
            case r if r.isGet && r.isGoingTo(s"alphavantage.com/$searchEndpoint") && r.hasParams(params) =>
              Response.ok(json("alpha-vantage/weekly-time-series-response.json"))
            case r => throw new RuntimeException(r.uri.toString())
          }

        val telegramClient = AlphaVantageClient.make[IO](config, testingBackend)
        val result         = telegramClient.flatMap(_.getWeeklyPrices(Ticker("PLTR")))

        result.unsafeToFuture().map { price =>
          price.ticker mustBe Ticker("PLTR")
          price.timeSeries mustBe TimeSeries.Weekly
        }
      }
    }
  }
}
