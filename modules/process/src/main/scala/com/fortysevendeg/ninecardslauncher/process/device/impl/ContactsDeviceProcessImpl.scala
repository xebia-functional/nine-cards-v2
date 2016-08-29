package com.fortysevendeg.ninecardslauncher.process.device.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.IterableContacts
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ServicesContact, ContactCounter}
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ImplicitsContactsServiceExceptions}

import scalaz.concurrent.Task

trait ContactsDeviceProcessImpl extends DeviceProcess {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsContactsServiceExceptions =>

  val emptyContactCounterService: TaskService[Seq[ContactCounter]] =
    TaskService(Task(Xor.Right(Seq.empty)))

  def getFavoriteContacts(implicit context: ContextSupport) =
    (for {
      favoriteContacts <- contactsServices.getFavoriteContacts
      filledFavoriteContacts <- fillContacts(favoriteContacts)
    } yield toContactSeq(filledFavoriteContacts)).resolve[ContactException]

  def getContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
    (for {
      contacts <- filter match {
        case AllContacts => contactsServices.getContacts
        case FavoriteContacts => contactsServices.getFavoriteContacts
        case ContactsWithPhoneNumber => contactsServices.getContactsWithPhone
      }
    } yield toContactSeq(contacts)).resolve[ContactException]

  def getTermCountersForContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
    (for {
      counters <- filter match {
        case AllContacts => contactsServices.getAlphabeticalCounterContacts
        case FavoriteContacts => emptyContactCounterService
        case ContactsWithPhoneNumber => emptyContactCounterService
      }
    } yield counters map toTermCounter).resolve[ContactException]

  def getIterableContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
    (for {
      iter <- filter match {
        case AllContacts => contactsServices.getIterableContacts
        case FavoriteContacts => contactsServices.getIterableFavoriteContacts
        case ContactsWithPhoneNumber => contactsServices.getIterableContactsWithPhone
      }
    } yield new IterableContacts(iter)).resolve[ContactException]

  def getIterableContactsByKeyWord(keyword: String)(implicit context: ContextSupport)  =
    (for {
      iter <- contactsServices.getIterableContactsByKeyword(keyword)
    } yield new IterableContacts(iter)).resolve[ContactException]

  def getContact(lookupKey: String)(implicit context: ContextSupport) =
    (for {
      contact <- contactsServices.findContactByLookupKey(lookupKey)
    } yield toContact(contact)).resolve[ContactException]

  // TODO Change when ticket is finished (9C-235 - Fetch contacts from several lookup keys)
  private[this] def fillContacts(contacts: Seq[ServicesContact]) = TaskService {
    val tasks = contacts map (c => contactsServices.findContactByLookupKey(c.lookupKey).value)
    Task.gatherUnordered(tasks) map (list => XorCatchAll[ContactsServiceException](list.collect { case Xor.Right(contact) => contact }))
  }.resolve[ContactException]
}
