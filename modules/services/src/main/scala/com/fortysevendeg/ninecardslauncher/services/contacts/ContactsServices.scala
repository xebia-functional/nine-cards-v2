package com.fortysevendeg.ninecardslauncher.services.contacts

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact, ContactCounter}

trait ContactsServices {

  /**
    * Get contacts sort by name. The info field is not filled
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getContacts: TaskService[Seq[Contact]]

  /**
    * Returns the number of times the first letter of a contact is repeated alphabetically
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.ContactCounter] contains
    *         information about the times is repeated a contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getAlphabeticalCounterContacts: TaskService[Seq[ContactCounter]]

  /**
    * Get iterable contacts sort by name. The info field is not filled
    *
    * @return the IterableCursorSeq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableContacts: TaskService[IterableCursor[Contact]]

  /**
    * Get iterable contacts by keyword sort by name. The info field is not filled
    *
    * @return the IterableCursorSeq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableContactsByKeyword(keyword: String): TaskService[IterableCursor[Contact]]

  /**
    * Return contact by email if exist. The info field is not filled
    *
    * @return the Option[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contact
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def fetchContactByEmail(email: String): TaskService[Option[Contact]]

  /**
    * Return contact by phone number if exist. The info field is not filled
    *
    * @return the Option[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contact
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def fetchContactByPhoneNumber(phoneNumber: String): TaskService[Option[Contact]]

  /**
    * Return contact by lookup key. The info field is filled
    *
    * @return the com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    * @throws ContactNotFoundException if the lookup key doesn't exits
    */
  def findContactByLookupKey(lookupKey: String): TaskService[Contact]

  /**
    * Populate the info field in every contact
    *
    * @return sequence of the com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    * @throws ContactNotFoundException if any contact doesn't exits
    */
  def populateContactInfo(contacts: Seq[Contact]): TaskService[Seq[Contact]]

  /**
    * Return favorite contacts. The info field is not filled
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getFavoriteContacts: TaskService[Seq[Contact]]

  /**
    * Return iterable favorite contacts. The info field is not filled
    *
    * @return the IterableCursor[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableFavoriteContacts: TaskService[IterableCursor[Contact]]

  /**
    * Return contacts with phone number. The info field is not filled
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getContactsWithPhone: TaskService[Seq[Contact]]

  /**
    * Return iterable contacts with phone number. The info field is not filled
    *
    * @return the IterableCursor[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableContactsWithPhone: TaskService[IterableCursor[Contact]]
}
