package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsContentProvider.{allFields, _}
import com.fortysevendeg.ninecardslauncher.services.contacts._
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{ContactCounter, ContactInfo}
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._

import scalaz.concurrent.Task

class ContactsServicesImpl(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator = new UriCreator)
  extends ContactsServices
  with ImplicitsContactsServiceExceptions {

  val abc = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ"

  val wildcard = "#"

  override def getContacts =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.fetchAll(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.ALL_CONTACTS_SELECTION,
            orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor))
        }
      }
    }

  override def getAlphabeticalCounterContacts =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          val iterator = getNamesAlphabetically
          iterator.foldLeft(Seq.empty[ContactCounter]) { (acc, name) =>
            val term = name.substring(0, 1).toUpperCase match {
              case t if abc.contains(t) => t
              case _ => wildcard
            }
            val lastWithSameTerm = acc.lastOption flatMap {
              case last if last.term == term => Some(last)
              case _ => None
            }
            lastWithSameTerm map { c =>
              acc.dropRight(1) :+ c.copy(count = c.count + 1)
            } getOrElse acc :+ ContactCounter(term, 1)
          }
        }
      }
    }

  override def getIterableContacts =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.getCursor(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.ALL_CONTACTS_SELECTION,
            orderBy = Fields.CONTACTS_ORDER_BY_ASC).toIterator(contactFromCursor)
        }
      }
    }

  override def getIterableContactsByKeyword(keyword: String) =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.getCursor(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.CONTACTS_BY_KEYWORD_SELECTION,
            whereParams = Seq(s"%$keyword%"),
            orderBy = Fields.CONTACTS_ORDER_BY_ASC).toIterator(contactFromCursor)
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
            orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor))
        }
      }
    }

  override def getIterableFavoriteContacts =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.getCursor(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.STARRED_SELECTION,
            orderBy = Fields.CONTACTS_ORDER_BY_ASC).toIterator(contactFromCursor)
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
            orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor))
        }
      }
    }

  override def getIterableContactsWithPhone =
    Service {
      Task {
        CatchAll[ContactsServiceException] {
          contentResolverWrapper.getCursor(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.HAS_PHONE_NUMBER_SELECTION,
            orderBy = Fields.CONTACTS_ORDER_BY_ASC).toIterator(contactFromCursor)
        }
      }
    }

  protected def getNamesAlphabetically: Seq[String] = {
    getListFromCursor(nameFromCursor)(contentResolverWrapper.getCursor(
      uri = Fields.CONTENT_URI,
      projection = Seq(Fields.DISPLAY_NAME),
      where = Fields.ALL_CONTACTS_SELECTION,
      orderBy = Fields.CONTACTS_ORDER_BY_ASC))
  }

}
