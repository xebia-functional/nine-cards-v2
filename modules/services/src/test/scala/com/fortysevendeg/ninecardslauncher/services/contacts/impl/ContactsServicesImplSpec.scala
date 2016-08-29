package com.fortysevendeg.ninecardslauncher.services.contacts.impl

import android.net.Uri
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsContentProvider._
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactNotFoundException, ContactsServiceException, Fields}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ContactsServicesSpecification
  extends Specification
    with Mockito
    with ContactsServicesImplData {

  val contentResolverException = new RuntimeException("Irrelevant message")

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

  trait ValidContactsServicesResponses
    extends ContactsServicesScope {

    contentResolverWrapper.fetchAll(
      uri = Fields.CONTENT_URI,
      projection = allFields,
      where = Fields.ALL_CONTACTS_SELECTION,
      orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor)) returns contacts

    contentResolverWrapper.fetch(
      uri = Fields.EMAIL_CONTENT_URI,
      projection = allEmailContactFields,
      where = Fields.EMAIL_SELECTION,
      whereParams = Seq(emailHome))(getEntityFromCursor(contactFromEmailCursor)) returns contactWithEmail

    contentResolverWrapper.fetch(
      uri = Fields.EMAIL_CONTENT_URI,
      projection = allEmailContactFields,
      where = Fields.EMAIL_SELECTION,
      whereParams = Seq(nonExistentEmail))(getEntityFromCursor(contactFromEmailCursor)) returns None

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allPhoneContactFields)(getEntityFromCursor(contactFromPhoneCursor)) returns contactWithPhone

    contentResolverWrapper.fetch(
      uri = nonExistentMockUri,
      projection = allPhoneContactFields)(getEntityFromCursor(contactFromPhoneCursor)) returns None

    contentResolverWrapper.fetch(
      uri = Fields.CONTENT_URI,
      projection = allFields,
      where = Fields.LOOKUP_SELECTION,
      whereParams = Seq(firstLookupKey))(getEntityFromCursor(contactFromCursor)) returns contact

    contentResolverWrapper.fetchAll(
      uri = Fields.EMAIL_CONTENT_URI,
      projection = allEmailFields,
      where = Fields.EMAIL_CONTACT_SELECTION,
      whereParams = Seq(firstLookupKey))(getListFromCursor(emailFromCursor)) returns contactEmails

    contentResolverWrapper.fetchAll(
      uri = Fields.PHONE_CONTENT_URI,
      projection = allPhoneFields,
      where = Fields.PHONE_CONTACT_SELECTION,
      whereParams = Seq(firstLookupKey))(getListFromCursor(phoneFromCursor)) returns contactPhones

    contentResolverWrapper.fetch(
      uri = Fields.CONTENT_URI,
      projection = allFields,
      where = Fields.LOOKUP_SELECTION,
      whereParams = Seq(nonExistentLookupKey))(getEntityFromCursor(contactFromCursor)) returns None

  }

  trait ErrorContactsServicesResponses
    extends ContactsServicesScope
      with ContactsServicesImplData {

    contentResolverWrapper.fetchAll(
      uri = Fields.CONTENT_URI,
      projection = allFields,
      where = Fields.ALL_CONTACTS_SELECTION,
      orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      uri = Fields.EMAIL_CONTENT_URI,
      projection = allEmailContactFields,
      where = Fields.EMAIL_SELECTION,
      whereParams = Seq(emailHome))(getEntityFromCursor(contactFromEmailCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allPhoneContactFields)(getEntityFromCursor(contactFromPhoneCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      uri = Fields.CONTENT_URI,
      projection = allFields,
      where = Fields.LOOKUP_SELECTION,
      whereParams = Seq(firstLookupKey))(getEntityFromCursor(contactFromCursor)) throws contentResolverException

  }

  trait ErrorIteratorContactsServicesResponses
    extends ContactsServicesScope
      with ContactsServicesImplData {

    lazy val contactsServicesException = new ContactsServicesImpl(contentResolverWrapper, uriCreator) {
      override protected def getNamesAlphabetically: Seq[String] =
        throw contentResolverException
    }
  }

  trait FavoriteContactsServicesResponses
    extends ContactsServicesScope
      with ContactsServicesImplData {

    contentResolverWrapper.fetchAll(
      Fields.CONTENT_URI,
      allFields,
      where = Fields.STARRED_SELECTION,
      orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor)) returns contacts
  }

  trait ErrorFavoriteContactsServicesResponses
    extends ErrorContactsServicesResponses
      with ContactsServicesImplData {

    contentResolverWrapper.fetchAll(
      Fields.CONTENT_URI,
      allFields,
      where = Fields.STARRED_SELECTION,
      orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor)) throws contentResolverException

  }

  trait ContactsWithPhoneServicesResponses
    extends ContactsServicesScope
      with ContactsServicesImplData {

    contentResolverWrapper.fetchAll(
      Fields.CONTENT_URI,
      allFields,
      where = Fields.HAS_PHONE_NUMBER_SELECTION,
      orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor)) returns contacts
  }

  trait ErrorContactsWithPhoneServicesResponses
    extends ErrorContactsServicesResponses
      with ContactsServicesImplData {

    contentResolverWrapper.fetchAll(
      Fields.CONTENT_URI,
      allFields,
      where = Fields.HAS_PHONE_NUMBER_SELECTION,
      orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor)) throws contentResolverException

  }

}

class ContactsServicesImplSpec
  extends ContactsServicesSpecification {

  "ContactsService component" should {

    "getContacts" should {

      "returns all the contacts from the content resolver" in
        new ValidContactsServicesResponses {
          val result = contactsServices.getContacts.value.run

          result must beLike {
            case Xor.Right(seq) => seq shouldEqual contacts
          }
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ErrorContactsServicesResponses {
          val result = contactsServices.getContacts.value.run

          result must beLike {
            case Xor.Left(e) =>  e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
        }

    }

    "getAlphabeticalCounterContacts" should {

      "return a sequence of ContactCounter sort alphabetically" in
        new ValidContactsServicesResponses {
          val result = contactsServices.getAlphabeticalCounterContacts.value.run

          result must beLike {
            case Xor.Right(seq) => seq shouldEqual contactCounters
          }
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ErrorIteratorContactsServicesResponses {
          val result = contactsServicesException.getAlphabeticalCounterContacts.value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }

    }

    "fetchContactByEmail" should {

      "return the contact from the content resolver for an existent email" in
        new ValidContactsServicesResponses {
          val result = contactsServices.fetchContactByEmail(emailHome).value.run

          result must beLike {
            case Xor.Right(c) => c must beEqualTo(contactWithEmail)
          }
        }

      "return None from the content resolver for a non existent email" in
        new ValidContactsServicesResponses {
          val result = contactsServices.fetchContactByEmail(nonExistentEmail).value.run

          result must beLike {
            case Xor.Right(c) => c must beNone
          }
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ErrorContactsServicesResponses {
          val result = contactsServices.fetchContactByEmail(emailHome).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }

    }

    "fetchContactByPhoneNumber" should {

      "return the contact from the content resolver for an existent phone" in
        new ValidContactsServicesResponses {
          val result = contactsServices.fetchContactByPhoneNumber(phoneHome).value.run

          result must beLike {
            case Xor.Right(c) => c must beEqualTo(contactWithPhone)
          }
        }

      "return None from the content resolver for a non existent email" in
        new ValidContactsServicesResponses {
          val result = contactsServices.fetchContactByPhoneNumber(nonExistentPhone).value.run

          result must beLike {
            case Xor.Right(c) => c must beNone
          }
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ErrorContactsServicesResponses {
          val result = contactsServices.fetchContactByPhoneNumber(phoneHome).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }

    }

    "findContactByLookupKey" should {

      "return the contact from the content resolver for an existent lookup key" in
        new ValidContactsServicesResponses {
          val result = contactsServices.findContactByLookupKey(firstLookupKey).value.run
          result must beLike {
            case Xor.Right(c) => Some(c) shouldEqual contact
          }
        }

      "return a ContactsServiceException with a ContactNotFoundException as cause for a non existent lookup key" in
        new ValidContactsServicesResponses {
          val result = contactsServices.findContactByLookupKey(nonExistentLookupKey).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ must beAnInstanceOf[ContactNotFoundException])
          }
        }

      "return a ContactNotFoundException when the content resolver throws an exception" in
        new ErrorContactsServicesResponses {
          val result = contactsServices.findContactByLookupKey(firstLookupKey).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }

    }

    "getFavoriteContacts" should {

      "returns favorites contacts from the content resolver" in
        new FavoriteContactsServicesResponses {
          val result = contactsServices.getFavoriteContacts.value.run

          result must beLike {
            case Xor.Right(seq) => seq shouldEqual contacts
          }
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ErrorFavoriteContactsServicesResponses {
          val result = contactsServices.getFavoriteContacts.value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }

    }

    "getContactsWithPhone" should {

      "returns contacts with phone numbers from the content resolver" in
        new ContactsWithPhoneServicesResponses {
          val result = contactsServices.getContactsWithPhone.value.run

          result must beLike {
            case Xor.Right(seq) => seq shouldEqual contacts
          }
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ErrorContactsWithPhoneServicesResponses {
          val result = contactsServices.getContactsWithPhone.value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }

    }

  }

}
