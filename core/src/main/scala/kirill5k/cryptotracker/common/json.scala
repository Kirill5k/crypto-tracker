package kirill5k.cryptotracker.common

import cats.effect.Sync
import io.circe.generic.extras.semiauto._
import io.circe.{Decoder, Encoder}
import kirill5k.cryptotracker.domain.{Subreddit, Ticker}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

import java.net.URI

object json extends JsonCodecs

trait JsonCodecs {
  implicit val srEncoder: Encoder[Subreddit] = deriveUnwrappedEncoder
  implicit val srDecoder: Decoder[Subreddit] = deriveUnwrappedDecoder

  implicit val tickEncoder: Encoder[Ticker] = deriveUnwrappedEncoder
  implicit val tickDecoder: Decoder[Ticker] = deriveUnwrappedDecoder

  implicit def deriveEntityEncoder[F[_], A: Encoder]: EntityEncoder[F, A]       = jsonEncoderOf[F, A]
  implicit def deriveEntityDecoder[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

  implicit val uriEncoder: Encoder[URI] = Encoder.encodeString.contramap[URI](_.toString)
}
