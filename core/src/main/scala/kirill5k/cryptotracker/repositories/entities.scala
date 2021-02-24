package kirill5k.cryptotracker.repositories

import kirill5k.cryptotracker.domain.{Mention, MentionSource, Subreddit, Ticker}
import org.bson.{BsonReader, BsonWriter}
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._

import java.net.URI
import java.time.Instant

private[repositories] object entities {

  val subredditCodecProvider = new CodecProvider {
    override def get[T](clazz: Class[T], registry: CodecRegistry): Codec[T] =
      if (clazz == classOf[Subreddit]) {
        new Codec[Subreddit] {
          override def encode(writer: BsonWriter, t: Subreddit, encoderContext: EncoderContext): Unit = writer.writeString(t.value)
          override def getEncoderClass: Class[Subreddit]                                              = classOf[Subreddit]
          override def decode(reader: BsonReader, decoderContext: DecoderContext): Subreddit          = Subreddit(reader.readString())
        }.asInstanceOf[Codec[T]]
      } else {
        null // scalastyle:ignore
      }
  }

  val tickerCodecProvider = new CodecProvider {
    override def get[T](clazz: Class[T], registry: CodecRegistry): Codec[T] =
      if (clazz == classOf[Ticker]) {
        new Codec[Ticker] {
          override def encode(writer: BsonWriter, t: Ticker, encoderContext: EncoderContext): Unit = writer.writeString(t.value)
          override def getEncoderClass: Class[Ticker]                                              = classOf[Ticker]
          override def decode(reader: BsonReader, decoderContext: DecoderContext): Ticker          = Ticker(reader.readString())
        }.asInstanceOf[Codec[T]]
      } else {
        null // scalastyle:ignore
      }
  }

  final case class MentionEntity(
      _id: ObjectId,
      ticker: Ticker,
      time: Instant,
      message: String,
      source: MentionSource,
      url: String
  ) {
    def toDomain: Mention =
      Mention(ticker, time, message, source, URI.create(url))
  }

  object MentionEntity {
    val codec = fromRegistries(
      fromProviders(
        classOf[MentionEntity],
        classOf[MentionSource],
        subredditCodecProvider,
        tickerCodecProvider
      ),
      DEFAULT_CODEC_REGISTRY
    )

    def from(m: Mention): MentionEntity =
      MentionEntity(new ObjectId(), m.ticker, m.time, m.message, m.source, m.url.toString)
  }
}
