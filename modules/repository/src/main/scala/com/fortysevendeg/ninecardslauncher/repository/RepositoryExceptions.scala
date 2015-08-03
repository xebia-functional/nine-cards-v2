package com.fortysevendeg.ninecardslauncher.repository

import scalaz.Scalaz._

object RepositoryExceptions {

  case class RepositoryException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
    cause map initCause
  }

  implicit def repositoryException = (t: Throwable) => RepositoryException(t.getMessage, t.some)

}