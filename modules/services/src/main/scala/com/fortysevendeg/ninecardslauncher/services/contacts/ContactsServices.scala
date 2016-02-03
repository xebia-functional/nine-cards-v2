package com.fortysevendeg.ninecardslauncher.services.contacts

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{ContactCounter, Contact}

trait ContactsServices {

  /**
    * Get contacts sort by name. The info field is not filled
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getContacts: ServiceDef2[Seq[Contact], ContactsServiceException]

  /**
    * Returns the number of times the first letter of a contact is repeated alphabetically
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.ContactCounter] contains
    *         information about the times is repeated a contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getAlphabeticalCounterContacts: ServiceDef2[Seq[ContactCounter], ContactsServiceException]

  /**
    * Get iterable contacts sort by name. The info field is not filled
    *
    * @return the IterableCursorSeq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableContacts: ServiceDef2[IterableCursor[Contact], ContactsServiceException]

  /**
    * Get iterable contacts by keyword sort by name. The info field is not filled
    *
    * @return the IterableCursorSeq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableContactsByKeyword(keyword: String): ServiceDef2[IterableCursor[Contact], ContactsServiceException]

  /**
    * Return contact by email if exist. The info field is not filled
    *
    * @return the Option[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contact
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def fetchContactByEmail(email: String): ServiceDef2[Option[Contact], ContactsServiceException]

  /**
    * Return contact by phone number if exist. The info field is not filled
    *
    * @return the Option[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contact
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def fetchContactByPhoneNumber(phoneNumber: String): ServiceDef2[Option[Contact], ContactsServiceException]

  /**
    * Return contact by lookup key. The info field is filled
    *
    * @return the com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider or lookup key don't exits
    */
  def findContactByLookupKey(lookupKey: String): ServiceDef2[Contact, ContactsServiceException]

  /**
    * Return favorite contacts. The info field is not filled
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getFavoriteContacts: ServiceDef2[Seq[Contact], ContactsServiceException]

  /**
    * Return iterable favorite contacts. The info field is not filled
    *
    * @return the IterableCursor[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableFavoriteContacts: ServiceDef2[IterableCursor[Contact], ContactsServiceException]

  /**
    * Return contacts with phone number. The info field is not filled
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getContactsWithPhone: ServiceDef2[Seq[Contact], ContactsServiceException]

  /**
    * Return iterable contacts with phone number. The info field is not filled
    *
    * @return the IterableCursor[com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact] contains
    *         information about contacts
    * @throws ContactsServiceException if exist some problem accessing to contact provider
    */
  def getIterableContactsWithPhone: ServiceDef2[IterableCursor[Contact], ContactsServiceException]
}
