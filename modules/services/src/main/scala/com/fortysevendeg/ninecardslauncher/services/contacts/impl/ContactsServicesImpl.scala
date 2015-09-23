package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsContentProvider.{allFields, _}
import com.fortysevendeg.ninecardslauncher.services.contacts._
import com.fortysevendeg.ninecardslauncher.services.contacts.models.ContactInfo

import scalaz.concurrent.Task

class ContactsServicesImpl(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator = new UriCreator)
  extends ContactsServices
  with ImplicitsContactsServiceExceptions {

  override def getContacts =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.fetchAll(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.ALL_CONTACTS_SELECTION,
            orderBy = s"${Fields.DISPLAY_NAME} asc")(getListFromCursor(contactFromCursor))
        }
      }
    }

  override def fetchContactByEmail(email: String) =
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

  override def fetchContactByPhoneNumber(phoneNumber: String) =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.fetch(
            uri = uriCreator.withAppendedPath(Fields.PHONE_LOOKUP_URI, phoneNumber),
            projection = allPhoneContactFields)(getEntityFromCursor(contactFromPhoneCursor))
        }
      }
    }

  override def findContactByLookupKey(lookupKey: String) =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
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
              val contactInfo = (emails, phones) match {
                case (Nil, Nil) => None
                case _ => Some(ContactInfo(emails, phones))
              }
              contact.copy(info = contactInfo)
            case _ => throw ContactNotFoundException(s"Contact with lookupKey=$lookupKey not found")
          }
        }
      }
    }

  override def getFavoriteContacts =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.fetchAll(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.STARRED_SELECTION,
            orderBy = s"${Fields.DISPLAY_NAME} asc")(getListFromCursor(contactFromCursor))
        }
      }
    }

  override def getContactsWithPhone =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.fetchAll(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.HAS_PHONE_NUMBER_SELECTION,
            orderBy = s"${Fields.DISPLAY_NAME} asc")(getListFromCursor(contactFromCursor))
        }
      }
    }
}
