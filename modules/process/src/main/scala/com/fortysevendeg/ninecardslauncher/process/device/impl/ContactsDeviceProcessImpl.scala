package com.fortysevendeg.ninecardslauncher.process.device.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.IterableContacts
import com.fortysevendeg.ninecardslauncher.services.contacts.models.ContactCounter
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsServicePermissionException

import scalaz.concurrent.Task

trait ContactsDeviceProcessImpl extends DeviceProcess {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException =>

  val emptyContactCounterService: TaskService[Seq[ContactCounter]] =
    TaskService(Task(Xor.Right(Seq.empty)))

  def mapServicesException[E >: NineCardException]: (NineCardException => E) = {
    case e: ContactsServicePermissionException => ContactPermissionException(e.message, Some(e))
    case e => ContactException(e.getMessage, Option(e))
  }

  def getFavoriteContacts(implicit context: ContextSupport) =
    (for {
      favoriteContacts <- contactsServices.getFavoriteContacts
      filledFavoriteContacts <- contactsServices.populateContactInfo(favoriteContacts)
    } yield toContactSeq(filledFavoriteContacts)).leftMap(mapServicesException)

  def getContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
    (for {
      contacts <- filter match {
        case AllContacts => contactsServices.getContacts
        case FavoriteContacts => contactsServices.getFavoriteContacts
        case ContactsWithPhoneNumber => contactsServices.getContactsWithPhone
      }
    } yield toContactSeq(contacts)).leftMap(mapServicesException)

  def getTermCountersForContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
    (for {
      counters <- filter match {
        case AllContacts => contactsServices.getAlphabeticalCounterContacts
        case FavoriteContacts => emptyContactCounterService
        case ContactsWithPhoneNumber => emptyContactCounterService
      }
    } yield counters map toTermCounter).leftMap(mapServicesException)

  def getIterableContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
    (for {
      iter <- filter match {
        case AllContacts => contactsServices.getIterableContacts
        case FavoriteContacts => contactsServices.getIterableFavoriteContacts
        case ContactsWithPhoneNumber => contactsServices.getIterableContactsWithPhone
      }
    } yield new IterableContacts(iter)).leftMap(mapServicesException)

  def getIterableContactsByKeyWord(keyword: String)(implicit context: ContextSupport)  =
    (for {
      iter <- contactsServices.getIterableContactsByKeyword(keyword)
    } yield new IterableContacts(iter)).resolve[ContactException]

  def getContact(lookupKey: String)(implicit context: ContextSupport) =
    (for {
      contact <- contactsServices.findContactByLookupKey(lookupKey)
    } yield toContact(contact)).leftMap(mapServicesException)

}
