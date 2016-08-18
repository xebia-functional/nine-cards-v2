package com.fortysevendeg.ninecardslauncher.services.contacts

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact, ContactCounter}

trait ContactsServices {

  /**
    * Get contacts sort by name. The info field is not filled
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getContacts: CatsService[Seq[Contact]]

  /**
    * Returns the number of times the first letter of a contact is repeated alphabetically
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.ContactCounter] contains
    *         information about the times is repeated a contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getAlphabeticalCounterContacts: CatsService[Seq[ContactCounter]]

  /**
    * Get iterable contacts sort by name. The info field is not filled
    *
    * @return the IterableCursorSeq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableContacts: CatsService[IterableCursor[Contact]]

  /**
    * Get iterable contacts by keyword sort by name. The info field is not filled
    *
    * @return the IterableCursorSeq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableContactsByKeyword(keyword: String): CatsService[IterableCursor[Contact]]

  /**
    * Return contact by email if exist. The info field is not filled
    *
    * @return the Option[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contact
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def fetchContactByEmail(email: String): CatsService[Option[Contact]]

  /**
    * Return contact by phone number if exist. The info field is not filled
    *
    * @return the Option[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contact
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def fetchContactByPhoneNumber(phoneNumber: String): CatsService[Option[Contact]]

  /**
    * Return contact by lookup key. The info field is filled
    *
    * @return the com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider or lookup key don't exits
    */
  def findContactByLookupKey(lookupKey: String): CatsService[Contact]

  /**
    * Return favorite contacts. The info field is not filled
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getFavoriteContacts: CatsService[Seq[Contact]]

  /**
    * Return iterable favorite contacts. The info field is not filled
    *
    * @return the IterableCursor[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableFavoriteContacts: CatsService[IterableCursor[Contact]]

  /**
    * Return contacts with phone number. The info field is not filled
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getContactsWithPhone: CatsService[Seq[Contact]]

  /**
    * Return iterable contacts with phone number. The info field is not filled
    *
    * @return the IterableCursor[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableContactsWithPhone: CatsService[IterableCursor[Contact]]
}
