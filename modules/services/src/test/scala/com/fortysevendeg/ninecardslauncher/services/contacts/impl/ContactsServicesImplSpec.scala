package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import android.net.Uri
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsContentProvider._
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{ContactPhone, Contact}
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactNotFoundException, ContactsServiceException, Fields}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ContactsServicesSpecification
  extends Specification
    with Mockito
    with ContactsServicesImplData {

  val contentResolverException = new ContactsServiceException("Irrelevant message")

  trait ContactsServicesScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val mockUri = mock[Uri]

    lazy val nonExistentMockUri = mock[Uri]

    uriCreator.withAppendedPath(Fields.PHONE_LOOKUP_URI, phoneHome) returns mockUri

    uriCreator.withAppendedPath(Fields.PHONE_LOOKUP_URI, nonExistentPhone) returns nonExistentMockUri

    lazy val contactsServices = new ContactsServicesImpl(contentResolverWrapper, uriCreator) {
      override protected def getNamesAlphabetically: Seq[String] = contactsIterator
    }

  }

}

class ContactsServicesImplSpec
  extends ContactsServicesSpecification {

  "ContactsService component" should {

    "getContacts" should {

      "returns all the contacts from the content resolver" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll[Contact](any,any,any,any,any)(any) returns contacts
          val result = contactsServices.getContacts.value.run
          result shouldEqual Xor.Right(contacts)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(any,any,any,any,any)(any) throws contentResolverException
          val result = contactsServices.getContacts.value.run
          result must beAnInstanceOf[Xor.Left[ContactsServiceException]]
        }
    }

    "getAlphabeticalCounterContacts" should {

      "return a sequence of ContactCounter sort alphabetically" in
        new ContactsServicesScope {

          val result = contactsServices.getAlphabeticalCounterContacts.value.run
          result shouldEqual Xor.Right(contactCounters)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          lazy val contactsServicesException = new ContactsServicesImpl(contentResolverWrapper, uriCreator) {
            override protected def getNamesAlphabetically: Seq[String] =
              throw contentResolverException
          }
          val result = contactsServicesException.getAlphabeticalCounterContacts.value.run
          result must beAnInstanceOf[Xor.Left[ContactsServiceException]]
        }
    }

    "fetchContactByEmail" should {

      "return the contact from the content resolver for an existent email" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch[Contact](any,any,any,any,any)(any) returns contactWithEmail
          val result = contactsServices.fetchContactByEmail(emailHome).value.run
          result shouldEqual Xor.Right(contactWithEmail)
        }

      "return None from the content resolver for a non existent email" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch(any,any,any,any,any)(any) returns None
          val result = contactsServices.fetchContactByEmail(nonExistentEmail).value.run
          result shouldEqual Xor.Right(None)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch(any,any,any,any,any)(any) throws contentResolverException
          val result = contactsServices.fetchContactByEmail(emailHome).value.run
          result must beAnInstanceOf[Xor.Left[ContactsServiceException]]
        }

    }

    "fetchContactByPhoneNumber" should {

      "return the contact from the content resolver for an existent phone" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch[Contact](any,any,any,any,any)(any) returns contactWithPhone
          val result = contactsServices.fetchContactByPhoneNumber(phoneHome).value.run
          result shouldEqual Xor.Right(contactWithPhone)
        }

      "return None from the content resolver for a non existent email" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch(any,any,any,any,any)(any) returns None
          val result = contactsServices.fetchContactByPhoneNumber(nonExistentPhone).value.run
          result shouldEqual Xor.Right(None)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch(any,any,any,any,any)(any) throws contentResolverException
          val result = contactsServices.fetchContactByPhoneNumber(phoneHome).value.run
          result must beAnInstanceOf[Xor.Left[ContactsServiceException]]
        }
    }

    "findContactByLookupKey" should {

      "return the contact from the content resolver for an existent lookup key" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch[Contact](any,any,any,any,any)(any) returns contact

          contentResolverWrapper.fetchAll[ContactPhone](any,any,any,any,any)(any) returns contactPhones

          contentResolverWrapper.fetchAll(
            uri = Fields.EMAIL_CONTENT_URI,
            projection = allEmailFields,
            where = Fields.EMAIL_CONTACT_SELECTION,
            whereParams = Seq(firstLookupKey))(getListFromCursor(emailFromCursor)) returns contactEmails

          val result = contactsServices.findContactByLookupKey(firstLookupKey).value.run
          result must beLike {
            case Xor.Right(c) => Some(c) shouldEqual contact
          }
        }

      "return a ContactsServiceException with a ContactNotFoundException as cause for a non existent lookup key" in
        new ContactsServicesScope {

          val result = contactsServices.findContactByLookupKey(nonExistentLookupKey).value.run
          result must beAnInstanceOf[Xor.Left[ContactNotFoundException]]
        }

      "return a ContactNotFoundException when the content resolver throws an exception" in
        new ContactsServicesScope {

          val result = contactsServices.findContactByLookupKey(firstLookupKey).value.run
          result must beAnInstanceOf[Xor.Left[ContactsServiceException]]
        }

    }

    "getFavoriteContacts" should {

      "returns favorites contacts from the content resolver" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll[Contact](any,any,any,any,any)(any) returns contacts
          val result = contactsServices.getFavoriteContacts.value.run
          result shouldEqual Xor.Right(contacts)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(any,any,any,any,any)(any) throws contentResolverException
          val result = contactsServices.getFavoriteContacts.value.run
          result must beAnInstanceOf[Xor.Left[ContactsServiceException]]
        }

    }

    "getContactsWithPhone" should {

      "returns contacts with phone numbers from the content resolver" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll[Contact](any,any,any,any,any)(any) returns contacts
          val result = contactsServices.getContactsWithPhone.value.run
          result shouldEqual Xor.Right(contacts)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(any,any,any,any,any)(any) throws contentResolverException
          val result = contactsServices.getContactsWithPhone.value.run
          result must beAnInstanceOf[Xor.Left[ContactsServiceException]]
        }

    }

  }

}
