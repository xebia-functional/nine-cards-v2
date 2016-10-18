package cards.nine.services.contacts.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contentresolver.Conversions._
import cards.nine.commons.contentresolver.IterableCursor._
import cards.nine.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models._
import cards.nine.services.contacts.ContactsContentProvider.{allFields, _}
import cards.nine.services.contacts._
import cats.syntax.either._
import monix.eval.Task

class ContactsServicesImpl(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator = new UriCreator)
  extends ContactsServices {

  val abc = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ"

  val wildcard = "#"

  override def getContacts =
    catchMapPermission {
      contentResolverWrapper.fetchAll(
        uri = Fields.CONTENT_URI,
        projection = allFields,
        where = Fields.ALL_CONTACTS_SELECTION,
        orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor(uriCreator, _)))
    }

  override def getAlphabeticalCounterContacts =
    catchMapPermission {
      val iterator = getNamesAlphabetically
      iterator.foldLeft(Seq.empty[TermCounter]) { (acc, name) =>
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
        } getOrElse acc :+ TermCounter(term, 1)
      }
    }

  override def getIterableContacts =
    catchMapPermission {
      contentResolverWrapper.getCursor(
        uri = Fields.CONTENT_URI,
        projection = allFields,
        where = Fields.ALL_CONTACTS_SELECTION,
        orderBy = Fields.CONTACTS_ORDER_BY_ASC).toIterator(contactFromCursor(uriCreator, _))
    }

  override def getIterableContactsByKeyword(keyword: String) =
    catchMapPermission {
      contentResolverWrapper.getCursor(
        uri = Fields.CONTENT_URI,
        projection = allFields,
        where = Fields.CONTACTS_BY_KEYWORD_SELECTION,
        whereParams = Seq(s"%$keyword%"),
        orderBy = Fields.CONTACTS_ORDER_BY_ASC).toIterator(contactFromCursor(uriCreator, _))
    }

  override def fetchContactByEmail(email: String) =
    catchMapPermission {
      contentResolverWrapper.fetch(
        uri = Fields.EMAIL_CONTENT_URI,
        projection = allEmailContactFields,
        where = Fields.EMAIL_SELECTION,
        whereParams = Seq(email))(getEntityFromCursor(contactFromEmailCursor(uriCreator, _)))
    }

  override def fetchContactByPhoneNumber(phoneNumber: String) =
    catchMapPermission {
      contentResolverWrapper.fetch(
        uri = uriCreator.withAppendedPath(Fields.PHONE_LOOKUP_URI, phoneNumber),
        projection = allPhoneContactFields)(getEntityFromCursor(contactFromPhoneCursor(uriCreator, _)))
    }

  override def findContactByLookupKey(lookupKey: String) = {

    val fetchContacts = () => contentResolverWrapper.fetchAll(
      uri = Fields.CONTENT_URI,
      projection = allFields,
      where = Fields.LOOKUP_SELECTION,
      whereParams = Seq(lookupKey))(getListFromCursor(contactFromCursor(uriCreator, _)))

    populateContacts(fetchContacts).resolveRight {
      _.headOption match {
        case Some(v) => Right(v)
        case None => Left(ContactNotFoundException(s"The lookupKey $lookupKey can't be found"))
      }
    }
  }

  override def populateContactInfo(contacts: Seq[Contact]) = populateContacts(() => contacts)

  private[this] def populateContacts(fetchContacts: () => Seq[Contact]): TaskService[Seq[Contact]] = {

    def mapValues[T](seq: Seq[(String, T)]): Map[String, Seq[T]] = seq.groupBy(_._1).mapValues(_.map(_._2))

    def emailAndPhones(lookupKeys: Seq[String]): (Map[String, Seq[ContactEmail]], Map[String, Seq[ContactPhone]]) = {

      val inArgs = lookupKeys.map(key => s"'$key'").mkString(",")

      (mapValues(contentResolverWrapper.fetchAll(
        uri = Fields.EMAIL_CONTENT_URI,
        projection = allEmailFields,
        where = s"${Fields.EMAIL_CONTACT_SELECTION} ($inArgs)")(getListFromCursor(lookupKeyAndEmailFromCursor))),
        mapValues(contentResolverWrapper.fetchAll(
          uri = Fields.PHONE_CONTENT_URI,
          projection = allPhoneFields,
          where = s"${Fields.PHONE_CONTACT_SELECTION} ($inArgs)")(getListFromCursor(lookupKeyAndPhoneFromCursor))))
    }

    catchMapPermission {
      val contacts = fetchContacts()
      val (emails, phones) = emailAndPhones(contacts.map(_.lookupKey))
      contacts map { contact =>
        (emails.getOrElse(contact.lookupKey, Seq.empty), phones.getOrElse(contact.lookupKey, Seq.empty)) match {
          case (contactEmails, contactPhones) if contactEmails.nonEmpty || contactPhones.nonEmpty =>
            contact.copy(info = Some(ContactInfo(contactEmails, contactPhones)))
          case _ =>
            contact
        }
      }
    }
  }

  override def getFavoriteContacts =
    catchMapPermission {
      contentResolverWrapper.fetchAll(
        uri = Fields.CONTENT_URI,
        projection = allFields,
        where = Fields.STARRED_SELECTION,
        orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor(uriCreator, _)))
    }

  override def getIterableFavoriteContacts =
    catchMapPermission {
      contentResolverWrapper.getCursor(
        uri = Fields.CONTENT_URI,
        projection = allFields,
        where = Fields.STARRED_SELECTION,
        orderBy = Fields.CONTACTS_ORDER_BY_ASC).toIterator(contactFromCursor(uriCreator, _))
    }

  override def getContactsWithPhone =
    catchMapPermission {
      contentResolverWrapper.fetchAll(
        uri = Fields.CONTENT_URI,
        projection = allFields,
        where = Fields.HAS_PHONE_NUMBER_SELECTION,
        orderBy = Fields.CONTACTS_ORDER_BY_ASC)(getListFromCursor(contactFromCursor(uriCreator, _)))
    }

  override def getIterableContactsWithPhone =
    catchMapPermission {
      contentResolverWrapper.getCursor(
        uri = Fields.CONTENT_URI,
        projection = allFields,
        where = Fields.HAS_PHONE_NUMBER_SELECTION,
        orderBy = Fields.CONTACTS_ORDER_BY_ASC).toIterator(contactFromCursor(uriCreator, _))
    }

  protected def getNamesAlphabetically: Seq[String] = {
    getListFromCursor(nameFromCursor)(contentResolverWrapper.getCursor(
      uri = Fields.CONTENT_URI,
      projection = Seq(Fields.DISPLAY_NAME),
      where = Fields.ALL_CONTACTS_SELECTION,
      orderBy = Fields.CONTACTS_ORDER_BY_ASC))
  }

  def catchMapPermission[V](f: => V) =
    TaskService {
      Task {
        Either.catchNonFatal(f) leftMap {
          case e: SecurityException => ContactsServicePermissionException(e.getMessage, Some(e))
          case e => ContactsServiceException(e.getMessage, Some(e))
        }
      }
    }

}
