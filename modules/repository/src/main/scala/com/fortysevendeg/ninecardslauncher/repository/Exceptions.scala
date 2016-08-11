package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

import scalaz.Scalaz._

case class RepositoryException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

trait ImplicitsRepositoryExceptions {
  implicit def repositoryException = (t: Throwable) => RepositoryException(t.getMessage, t.some)
}