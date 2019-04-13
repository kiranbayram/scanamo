package org.scanamo
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import org.scanamo.DynamoValue.DObject
import org.scanamo.error.DynamoReadError

object DynamoFormatX {
  def derive[A](
    d: Option[A],             // ^ default value
    decoder: DynamoDecoder[A], // ^ parser over AttributeValue
    encoder: A => DynamoValue, // ^ value encoder
  ): DynamoFormat[A] = ???


  implicit def toDynamoValue[A](pair: (String, A))(implicit D: DynamoFormat[A]): (String, DynamoValue) = (pair._1, D.write(pair._2))
}

sealed abstract class DynamoValue { self =>
  def toAttributeValue: AttributeValue = ???
}

object DynamoValue {
  import DynamoEncoder.KeyValue

  case object DNull extends DynamoValue
  final case class DBool(b: Boolean) extends DynamoValue
  final case class DObject(m: Map[KeyValue, Seq[KeyValue]]) extends DynamoValue
  // etc.
}

class DynamoDecoder[+A] { self =>
  def map[B](f: A => B): DynamoDecoder[B] = ???
  def flatMap[B](f: A => DynamoDecoder[B]): DynamoDecoder[B] = ???
  def zip[B](that: DynamoDecoder[B]): DynamoDecoder[(A, B)] = ???
  def ap[B](f: DynamoDecoder[A => B]): DynamoDecoder[B] = ???
  // ...
}
object DynamoDecoder {
  def apply[A](value: A): DynamoDecoder[A] = ???
  def fail(error: DynamoReadError): DynamoDecoder[Nothing] = ???
  def withObject[A](f: DObject => DynamoDecoder[A]): DynamoDecoder[A] = ???
}

object DynamoEncoder {
  type KeyValue = (String, DynamoValue)
  def getObject(p: KeyValue, ps: KeyValue*): DynamoValue = DObject(Map(p, ps))
}

object Test {
//  val decoder: DynamoDecoder[A] =
//    DynamoDecoder.withObject { obj =>
//      ( obj.int("foo")
//        , obj.string("bar")
//      ).mapN(A)
//    }
}
