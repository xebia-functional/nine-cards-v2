package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

case class CollectionException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class CardException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class ContactException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

trait ImplicitsCollectionException {
  implicit def collectionException = (t: Throwable) => CollectionException(t.getMessage, Option(t))

  implicit def cardException = (t: Throwable) => CardException(t.getMessage, Option(t))

  implicit def contactException = (t: Throwable) => ContactException(t.getMessage, Option(t))
}