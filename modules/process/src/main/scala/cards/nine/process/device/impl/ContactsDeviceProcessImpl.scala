package cards.nine.process.device.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.device._
import cards.nine.process.device.models.IterableContacts
import cards.nine.services.contacts.models.ContactCounter
import cards.nine.services.contacts.ContactsServicePermissionException
import monix.eval.Task
import cats.syntax.either._

trait ContactsDeviceProcessImpl extends DeviceProcess {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException =>

  val emptyContactCounterService: TaskService[Seq[ContactCounter]] =
    TaskService(Task(Right(Seq.empty)))

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
