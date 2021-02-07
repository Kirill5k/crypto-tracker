package kirill5k.cryptotracker.api

import kirill5k.cryptotracker.CatsIOSpec

import scala.io.Source

trait SttpClientSpec extends CatsIOSpec {

  def json(path: String): String =
    Source.fromResource(path).getLines.toList.mkString
}
