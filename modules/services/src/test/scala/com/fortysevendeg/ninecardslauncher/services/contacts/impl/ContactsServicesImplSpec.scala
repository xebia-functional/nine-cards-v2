package cards.nine.services.contacts.impl

import android.net.Uri
import cards.nine.commons.contentresolver.Conversions._
import cards.nine.commons.contentresolver.IterableCursor._
import cards.nine.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.repository.{IntDataType, StringDataType}
import cards.nine.models.{ContactPhone, ContactEmail, Contact}
import cards.nine.services.contacts.ContactsContentProvider._
import cards.nine.services.contacts.models.ContactPhone
import cards.nine.services.contacts.{ContactNotFoundException, ContactsServiceException, ContactsServicePermissionException, Fields}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ContactsServicesSpecification
  extends TaskServiceSpecification
  with Mockito
  with ContactsServicesImplData {

  val contentResolverException = new RuntimeException("Irrelevant message")

  val securityException = new SecurityException("Irrelevant message")

  trait ContactsServicesScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val mockUri = mock[Uri]

    lazy val nonExistentMockUri = mock[Uri]

    uriCreator.withAppendedPath(Fields.PHONE_LOOKUP_URI, phoneHome) returns mockUri

    uriCreator.withAppendedPath(Fields.PHONE_LOOKUP_URI, nonExistentPhone) returns nonExistentMockUri

    lazy val contactsServices = new ContactsServicesImpl(contentResolverWrapper, uriCreator)


  }

}

class ContactsServicesImplSpec
  extends ContactsServicesSpecification {

  "ContactsService component" should {

    "getContacts" should {

      "returns all the contacts from the content resolver" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll[Contact](any,any,any,any,any)(any) returns contacts
          val result = contactsServices.getContacts.run
          result shouldEqual Right(contacts)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(any,any,any,any,any)(any) throws contentResolverException
          val result = contactsServices.getContacts.run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }
    }

    "getAlphabeticalCounterContacts" should {

      "return a sequence of ContactCounter sort alphabetically" in
        new ContactsServicesScope with AlphabeticalMockCursor {

          contentResolverWrapper.getCursor(any, any, any, any, any) returns mockCursor

          val result = contactsServices.getAlphabeticalCounterContacts.run
          result shouldEqual Right(contactCounters)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          lazy val contactsServicesException = new ContactsServicesImpl(contentResolverWrapper, uriCreator) {
            override protected def getNamesAlphabetically: Seq[String] =
              throw contentResolverException
          }
          val result = contactsServicesException.getAlphabeticalCounterContacts.run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }
    }

    "getIterableContacts" should {

      "return an IterableCursor of contacts by keyword sort by name" in
      new ContactsServicesScope with ContactsMockCursor  {

        contentResolverWrapper.fetchAll[ContactEmail](any,any,any,any,any)(any) returns Seq.empty
        contentResolverWrapper.fetchAll[ContactPhone](any,any,any,any,any)(any) returns Seq.empty
        contentResolverWrapper.getCursor(any, any, any, any, any) returns mockCursor
        contacts.foreach { c =>
          val uri = mock[Uri]
          uriCreator.withAppendedPath(any, ===(c.lookupKey)) returns uri
          uri.toString returns c.photoUri
        }
        val result = contactsServices.getIterableContacts.run

        result must beLike {
          case Right(iterator) =>
            toSeq(iterator) shouldEqual contacts
        }
      }
      "return an a RepositoryException when a exception is thrown " in
        new AlphabeticalMockCursor with ContactsServicesScope {

          contentResolverWrapper.getCursor(any, any, any, any, any) throws contentResolverException
          val result = contactsServices.getIterableContacts.run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }
    }

    "getIterableContactsByKeyword" should {

      "return an IterableCursor of Contacts" in
        new ContactsServicesScope with ContactsMockCursor {

          contentResolverWrapper.fetchAll[ContactEmail](any,any,any,any,any)(any) returns Seq.empty
          contentResolverWrapper.fetchAll[ContactPhone](any,any,any,any,any)(any) returns Seq.empty
          contentResolverWrapper.getCursor(any, any, any, any, any) returns mockCursor
          contacts.foreach { c =>
            val uri = mock[Uri]
            uriCreator.withAppendedPath(any, ===(c.lookupKey)) returns uri
            uri.toString returns c.photoUri
          }
          val result = contactsServices.getIterableContactsByKeyword(keyword = testMockKeyword).run

          result must beLike {
            case Right(iterator) =>
              toSeq(iterator) shouldEqual contacts
          }
        }
      "return an a RepositoryException when a exception is thrown " in
        new AlphabeticalMockCursor with ContactsServicesScope {

          contentResolverWrapper.getCursor(any, any, any, any, any) throws contentResolverException
          val result = contactsServices.getIterableContactsByKeyword(keyword = testMockKeyword).run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }
    }

    "fetchContactByEmail" should {

      "return the contact from the content resolver for an existent email" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch[Contact](any,any,any,any,any)(any) returns contactWithEmail
          val result = contactsServices.fetchContactByEmail(emailHome).run
          result shouldEqual Right(contactWithEmail)
        }

      "return None from the content resolver for a non existent email" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch(any,any,any,any,any)(any) returns None
          val result = contactsServices.fetchContactByEmail(nonExistentEmail).run
          result shouldEqual Right(None)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch(any,any,any,any,any)(any) throws contentResolverException
          val result = contactsServices.fetchContactByEmail(emailHome).run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }

    }

    "fetchContactByPhoneNumber" should {

      "return the contact from the content resolver for an existent phone" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch[Contact](any,any,any,any,any)(any) returns contactWithPhone
          val result = contactsServices.fetchContactByPhoneNumber(phoneHome).run
          result shouldEqual Right(contactWithPhone)
        }

      "return None from the content resolver for a non existent email" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch(any,any,any,any,any)(any) returns None
          val result = contactsServices.fetchContactByPhoneNumber(nonExistentPhone).run
          result shouldEqual Right(None)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetch(any,any,any,any,any)(any) throws contentResolverException
          val result = contactsServices.fetchContactByPhoneNumber(phoneHome).run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }
    }

    "findContactByLookupKey" should {

      "return the contact from the content resolver for an existent lookup key" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(
            uri = Fields.CONTENT_URI,
            projection = allFields,
            where = Fields.LOOKUP_SELECTION,
            whereParams = Seq(firstLookupKey))(getListFromCursor(contactFromCursor(uriCreator = uriCreator, _))) returns Seq(contact)

          contentResolverWrapper.fetchAll(
            uri = Fields.EMAIL_CONTENT_URI,
            projection = allEmailFields,
            where = s"${Fields.EMAIL_CONTACT_SELECTION} ('$firstLookupKey')")(getListFromCursor(lookupKeyAndEmailFromCursor)) returns contactLookupKeyAndEmails

          contentResolverWrapper.fetchAll(
            uri = Fields.PHONE_CONTENT_URI,
            projection = allPhoneFields,
            where = s"${Fields.PHONE_CONTACT_SELECTION} ('$firstLookupKey')")(getListFromCursor(lookupKeyAndPhoneFromCursor)) returns contactLookupKeyAndPhones

          val result = contactsServices.findContactByLookupKey(firstLookupKey).run
          result shouldEqual Right(contact)
        }

      "return a ContactNotFoundException for a non existent lookup key" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(any, any, any, any, any)(any) returns Seq.empty
          val result = contactsServices.findContactByLookupKey(nonExistentLookupKey).run
          result must beAnInstanceOf[Left[ContactNotFoundException, _]]
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(any, any, any, any, any)(any) throws contentResolverException
          val result = contactsServices.findContactByLookupKey(firstLookupKey).run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }

    }

    "populateContactInfo" should {

      "return the contact from the content resolver with the info field populated" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(
            uri = Fields.EMAIL_CONTENT_URI,
            projection = allEmailFields,
            where = s"${Fields.EMAIL_CONTACT_SELECTION} ('$firstLookupKey')")(getListFromCursor(lookupKeyAndEmailFromCursor)) returns contactLookupKeyAndEmails

          contentResolverWrapper.fetchAll(
            uri = Fields.PHONE_CONTENT_URI,
            projection = allPhoneFields,
            where = s"${Fields.PHONE_CONTACT_SELECTION} ('$firstLookupKey')")(getListFromCursor(lookupKeyAndPhoneFromCursor)) returns contactLookupKeyAndPhones

          val result = contactsServices.populateContactInfo(Seq(contact.copy(info = None))).run
          result shouldEqual Right(Seq(contact))
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(any, any, any, any, any)(any) throws contentResolverException

          val result = contactsServices.findContactByLookupKey(firstLookupKey).run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }

    }

    "getFavoriteContacts" should {

      "returns favorites contacts from the content resolver" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll[Contact](any,any,any,any,any)(any) returns contacts
          val result = contactsServices.getFavoriteContacts.run
          result shouldEqual Right(contacts)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(any,any,any,any,any)(any) throws contentResolverException
          val result = contactsServices.getFavoriteContacts.run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }

    }
    "getIterableFavoriteContacts" should {

      "return iterable favorite contacts" in
        new ContactsServicesScope with ContactsMockCursor  {

          contentResolverWrapper.fetchAll[ContactEmail](any,any,any,any,any)(any) returns Seq.empty
          contentResolverWrapper.fetchAll[ContactPhone](any,any,any,any,any)(any) returns Seq.empty
          contentResolverWrapper.getCursor(any, any, any, any, any) returns mockCursor
          contacts.foreach { c =>
            val uri = mock[Uri]
            uriCreator.withAppendedPath(any, ===(c.lookupKey)) returns uri
            uri.toString returns c.photoUri
          }
          val result = contactsServices.getIterableFavoriteContacts.run

          result must beLike {
            case Right(iterator) =>
              toSeq(iterator) shouldEqual contacts
          }
        }
      "return an a RepositoryException when a exception is thrown " in
        new AlphabeticalMockCursor with ContactsServicesScope {

          contentResolverWrapper.getCursor(any, any, any, any, any) throws contentResolverException
          val result = contactsServices.getIterableFavoriteContacts.run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }
    }

    "getContactsWithPhone" should {

      "returns contacts with phone numbers from the content resolver" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll[Contact](any,any,any,any,any)(any) returns contacts
          val result = contactsServices.getContactsWithPhone.run
          result shouldEqual Right(contacts)
        }

      "return a ContactsServiceException when the content resolver throws an exception" in
        new ContactsServicesScope {

          contentResolverWrapper.fetchAll(any,any,any,any,any)(any) throws contentResolverException
          val result = contactsServices.getContactsWithPhone.run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }

    }
    "getIterableContactsWithPhone" should {

      "return iterable contacts with phone number" in
        new ContactsServicesScope with ContactsMockCursor  {

          contentResolverWrapper.fetchAll[ContactEmail](any,any,any,any,any)(any) returns Seq.empty
          contentResolverWrapper.fetchAll[ContactPhone](any,any,any,any,any)(any) returns Seq.empty
          contentResolverWrapper.getCursor(any, any, any, any, any) returns mockCursor
          contacts.foreach { c =>
            val uri = mock[Uri]
            uriCreator.withAppendedPath(any, ===(c.lookupKey)) returns uri
            uri.toString returns c.photoUri
          }
          val result = contactsServices.getIterableContactsWithPhone.run

          result must beLike {
            case Right(iterator) =>
              toSeq(iterator) shouldEqual contacts
          }
        }
      "return an a RepositoryException when a exception is thrown " in
        new AlphabeticalMockCursor with ContactsServicesScope {

          contentResolverWrapper.getCursor(any, any, any, any, any) throws contentResolverException
          val result = contactsServices.getIterableContactsWithPhone.run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }
    }

    "catchMapPermission" should {

      "return a Right value when the function doesn't throw any exception" in {
        new ContactsServicesScope {
          val value = "my-value"
          val result = contactsServices.catchMapPermission(value).run
          result shouldEqual Right(value)
        }
      }

      "return a ContactsServicePermissionException when the function throws a SecurityException" in {
        new ContactsServicesScope {
          val result = contactsServices.catchMapPermission(throw securityException).run
          result must beAnInstanceOf[Left[ContactsServicePermissionException, _]]
        }
      }

      "return a ContactsServiceException when the function throws a RuntimeException" in {
        new ContactsServicesScope {
          val result = contactsServices.catchMapPermission(throw contentResolverException).run
          result must beAnInstanceOf[Left[ContactsServiceException, _]]
        }
      }

    }

  }

}
