package com.fortysevendeg.ninecardslauncher.services.persistence

import scalaz.Scalaz._

case class AndroidIdNotFoundException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class InstallationNotFoundException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class PersistenceServiceException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class UserNotFoundException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsPersistenceServiceExceptions {

  implicit def androidIdNotFoundException = (t: Throwable) => AndroidIdNotFoundException(t.getMessage, t.some)

  implicit def installationNotFoundException = (t: Throwable) => InstallationNotFoundException(t.getMessage, t.some)

  implicit def persistenceServiceException = (t: Throwable) => PersistenceServiceException(t.getMessage, t.some)

  implicit def userNotFoundException = (t: Throwable) => UserNotFoundException(t.getMessage, t.some)
}
