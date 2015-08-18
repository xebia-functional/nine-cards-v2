package com.fortysevendeg.ninecardslauncher.services.contacts

import scalaz.Scalaz._

case class ContactsServiceException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class ContactNotFoundException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsContactsServiceExceptions {

  implicit def contactsServiceException = (t: Throwable) => ContactsServiceException(t.getMessage, t.some)

  implicit def contactNotFoundException = (t: Throwable) => ContactNotFoundException(t.getMessage, t.some)
}
