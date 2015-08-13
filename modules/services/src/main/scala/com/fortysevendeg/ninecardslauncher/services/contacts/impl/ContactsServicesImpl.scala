package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import android.database.SQLException
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
            uri = Fields.PHONE_CONTENT_URI,
            projection = allPhoneContactFields,
            where = Fields.PHONE_SELECTION,
            whereParams = Seq(phoneNumber))(getEntityFromCursor(contactFromPhoneCursor))
        }
      }
    }

  override def findContactById(id: Long): ServiceDef2[Contact, ContactNotFoundException] =
    Service {
      Task {
        CatchAll[ContactNotFoundException] {
          contentResolverWrapper.fetch(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.ID_SELECTION,
            whereParams = Seq(id.toString))(getEntityFromCursor(contactFromCursor)) match {
            case Some(contact) =>
              val emails = contentResolverWrapper.fetchAll(
                uri = Fields.EMAIL_CONTENT_URI,
                projection = allEmailFields,
                where = Fields.EMAIL_CONTACT_ID_SELECTION,
                whereParams = Seq(id.toString))(getListFromCursor(emailFromCursor))
              val phones = contentResolverWrapper.fetchAll(
                uri = Fields.PHONE_CONTENT_URI,
                projection = allPhoneFields,
                where = Fields.PHONE_CONTACT_ID_SELECTION,
                whereParams = Seq(id.toString))(getListFromCursor(phoneFromCursor))
              contact.copy(info = Some(ContactInfo(emails, phones)))
            case _ => throw new SQLException(s"Contact with user id=$id not found")
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
