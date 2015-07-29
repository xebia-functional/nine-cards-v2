package com.fortysevendeg.ninecardslauncher.services.persistence

import scalaz.Scalaz._

case class RepositoryException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsPersistenceExceptions {
  implicit def repositoryExceptionConverter = (t: Throwable) => RepositoryException(t.getMessage, t.some)
}