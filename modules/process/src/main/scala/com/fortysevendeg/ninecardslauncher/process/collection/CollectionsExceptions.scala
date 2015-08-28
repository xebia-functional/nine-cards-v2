package com.fortysevendeg.ninecardslauncher.process.collection

import scalaz.Scalaz._

case class CollectionException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class CardException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class ContactException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsCollectionException {
  implicit def collectionException = (t: Throwable) => CollectionException(t.getMessage, t.some)
  implicit def cardException = (t: Throwable) => CardException(t.getMessage, t.some)
  implicit def contactException = (t: Throwable) => ContactException(t.getMessage, t.some)
}