package com.fortysevendeg.ninecardslauncher.services.contacts

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact

trait ContactsServices {

  def getContacts: ServiceDef2[Seq[Contact], ContactsServiceException]

  def fetchContactByEmail(email: String): ServiceDef2[Option[Contact], ContactsServiceException]

  def fetchContactByPhoneNumber(phoneNumber: String): ServiceDef2[Option[Contact], ContactsServiceException]

  def findContactById(id: Long): ServiceDef2[Contact, ContactNotFoundException]

  def getFavoriteContacts: ServiceDef2[Seq[Contact], ContactsServiceException]
}
