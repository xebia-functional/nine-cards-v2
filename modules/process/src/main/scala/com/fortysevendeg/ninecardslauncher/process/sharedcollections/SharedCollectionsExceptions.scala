package com.fortysevendeg.ninecardslauncher.process.sharedcollections

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

case class SharedCollectionsExceptions(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsSharedCollectionsExceptions {
  implicit def sharedCollectionsExceptions = (t: Throwable) => SharedCollectionsExceptions(t.getMessage, Option(t))
}