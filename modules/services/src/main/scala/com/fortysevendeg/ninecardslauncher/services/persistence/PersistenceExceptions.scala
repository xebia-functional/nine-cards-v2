package com.fortysevendeg.ninecardslauncher.services.persistence

import scalaz.Scalaz._

object PersistenceExceptions {

  case class PersistenceServiceException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
    cause map initCause
  }

  implicit def persistenceServiceException = (t: Throwable) => PersistenceServiceException(t.getMessage, t.some)

  case class AndroidIdNotFoundException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
    cause map initCause
  }

  implicit def androidIdNotFoundException = (t: Throwable) => AndroidIdNotFoundException(t.getMessage, t.some)

  case class InstallationNotFoundException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
    cause map initCause
  }

  implicit def installationNotFoundException = (t: Throwable) => InstallationNotFoundException(t.getMessage, t.some)

  case class UserNotFoundException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
    cause map initCause
  }

  implicit def userNotFoundException = (t: Throwable) => UserNotFoundException(t.getMessage, t.some)
}
