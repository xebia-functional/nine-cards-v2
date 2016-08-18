package com.fortysevendeg.ninecardslauncher.services.contacts

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

case class ContactsServiceException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class ContactNotFoundException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

trait ImplicitsContactsServiceExceptions {

  implicit def contactsServiceException = (t: Throwable) => ContactsServiceException(t.getMessage, Option(t))

  implicit def contactNotFoundException = (t: Throwable) => ContactNotFoundException(t.getMessage, Option(t))
}
