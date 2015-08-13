package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import android.database.SQLException
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.ContentResolverWrapper
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsContentProvider.{allFields, _}
import com.fortysevendeg.ninecardslauncher.services.contacts._
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{ContactInfo, Contact}

import scalaz.concurrent.Task

class ContactsServicesImpl(contentResolverWrapper: ContentResolverWrapper)
  extends ContactsServices
  with ImplicitsContactsServiceExceptions {

  override def getContacts: ServiceDef2[Seq[Contact], ContactsServiceException] =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.fetchAll(
            uri = Fields.CONTENT_URI,
            projection = allFields)(getListFromCursor(contactFromCursor))
        }
      }
    }

  override def fetchContactByEmail(email: String): ServiceDef2[Option[Contact], ContactsServiceException] =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.fetch(
            uri = Fields.EMAIL_CONTENT_URI,
            projection = allEmailContactFields,
            where = Fields.EMAIL_SELECTION,
            whereParams = Seq(email))(getEntityFromCursor(contactFromEmailCursor))
        }
      }
    }

  override def fetchContactByPhoneNumber(phoneNumber: String): ServiceDef2[Option[Contact], ContactsServiceException] =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.fetch(
            uri = Uri.withAppendedPath(Fields.PHONE_LOOKUP_URI, Uri.encode(phoneNumber)),
            projection = allPhoneContactFields)(getEntityFromCursor(contactFromPhoneCursor))
        }
      }
    }

  override def findContactByLookupKey(lookupKey: String): ServiceDef2[Contact, ContactNotFoundException] =
    Service {
      Task {
        CatchAll[ContactNotFoundException] {
          contentResolverWrapper.fetch(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.LOOKUP_SELECTION,
            whereParams = Seq(lookupKey))(getEntityFromCursor(contactFromCursor)) match {
            case Some(contact) =>
              val emails = contentResolverWrapper.fetchAll(
                uri = Fields.EMAIL_CONTENT_URI,
                projection = allEmailFields,
                where = Fields.EMAIL_CONTACT_SELECTION,
                whereParams = Seq(lookupKey))(getListFromCursor(emailFromCursor))
              val phones = contentResolverWrapper.fetchAll(
                uri = Fields.PHONE_CONTENT_URI,
                projection = allPhoneFields,
                where = Fields.PHONE_CONTACT_SELECTION,
                whereParams = Seq(lookupKey))(getListFromCursor(phoneFromCursor))
              contact.copy(info = Some(ContactInfo(emails, phones)))
            case _ => throw new SQLException("Contact not found")
          }
        }
      }
    }

  override def getFavoriteContacts: ServiceDef2[Seq[Contact], ContactsServiceException] =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.fetchAll(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.STARRED_SELECTION)(getListFromCursor(contactFromCursor))
        }
      }
    }
}
