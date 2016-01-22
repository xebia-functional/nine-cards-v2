package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.{CatchAll, _}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.IterableContacts
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ServicesContact, ContactCounter}
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ImplicitsContactsServiceExceptions}
import rapture.core.Answer

import scalaz.concurrent.Task

trait ContactsDeviceProcessImpl {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsContactsServiceExceptions =>

  val emptyContactCounterService: ServiceDef2[Seq[ContactCounter], ContactsServiceException] =
    Service(Task(Answer(Seq.empty)))

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

  def getCounterForIterableContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
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
  private[this] def fillContacts(contacts: Seq[ServicesContact]) = Service {
    val tasks = contacts map (c => contactsServices.findContactByLookupKey(c.lookupKey).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[ContactsServiceException](list.collect { case Answer(contact) => contact }))
  }.resolve[ContactException]
}
