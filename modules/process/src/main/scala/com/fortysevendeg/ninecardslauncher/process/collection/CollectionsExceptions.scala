package com.fortysevendeg.ninecardslauncher.process.collection

import scalaz.Scalaz._

case class CollectionException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsCollectionException {
  implicit def collectionException = (t: Throwable) => CollectionException(t.getMessage, t.some)
}