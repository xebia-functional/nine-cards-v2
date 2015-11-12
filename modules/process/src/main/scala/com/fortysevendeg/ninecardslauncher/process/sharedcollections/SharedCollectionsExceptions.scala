package com.fortysevendeg.ninecardslauncher.process.sharedcollections

import scalaz.Scalaz._

case class SharedCollectionsExceptions(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsSharedCollectionsExceptions {
  implicit def sharedCollectionsExceptions = (t: Throwable) => SharedCollectionsExceptions(t.getMessage, t.some)
}