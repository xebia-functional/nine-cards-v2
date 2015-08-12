package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.ContentResolverWrapper
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.commons.CacheCategoryUri
import com.fortysevendeg.ninecardslauncher.repository.provider.CacheCategoryEntity
import com.fortysevendeg.ninecardslauncher.repository.provider.CacheCategoryEntity._
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsContentProvider._
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsContentProvider.allFields
import com.fortysevendeg.ninecardslauncher.services.contacts._
import com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact

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
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = s"${Fields.packageName} = ?",
            whereParams = Seq(packageName))(getEntityFromCursor(cacheCategoryEntityFromCursor)) map toCacheCategory
        }
      }
    }

  override def findContactById(id: Long): ServiceDef2[Contact, ContactNotFoundException] = ???

  override def fetchContactByPhoneNumber(phoneNumber: String): ServiceDef2[Option[Contact], ContactsServiceException] = ???

  override def getFavoriteContacts: ServiceDef2[Seq[Contact], ContactsServiceException] = ???
}
