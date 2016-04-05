package com.fortysevendeg.ninecardslauncher.process.collection

import scalaz.Scalaz._

trait CollectionException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class CollectionExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with CollectionException {
  cause map initCause
}

case class CardException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class ContactException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsCollectionException {
  implicit def collectionException = (t: Throwable) => CollectionExceptionImpl(t.getMessage, t.some)
  implicit def cardException = (t: Throwable) => CardException(t.getMessage, t.some)
  implicit def contactException = (t: Throwable) => ContactException(t.getMessage, t.some)
}