package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.collection._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{FormedCollection, UnformedItem}
import com.fortysevendeg.ninecardslauncher.process.collection.utils.NineCardAppUtils._
import com.fortysevendeg.ninecardslauncher.process.commons.{NineCardCategories, CollectionType}
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.services.contacts.{ImplicitsContactsServiceExceptions, ContactsServiceException, ContactsServices}
import com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, ImplicitsPersistenceServiceExceptions, AddCollectionRequest, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import rapture.core.Answer

import scala.annotation.tailrec
import scalaz.{\/-, -\/}
import scalaz.concurrent.Task

class CollectionProcessImpl(
  collectionProcessConfig: CollectionProcessConfig,
  persistenceServices: PersistenceServices,
  contactsServices: ContactsServices)
  extends CollectionProcess
  with ImplicitsCollectionException
  with ImplicitsPersistenceServiceExceptions
  with ImplicitsContactsServiceExceptions
  with Conversions {

  val formedCollectionConversions = new FormedCollectionConversions(new ResourceUtils, contactsServices)

  override def createCollectionsFromUnformedItems(items: Seq[UnformedItem])(implicit context: ContextSupport) = Service {
    val tasks = createCollections(items, categories) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }.resolve[CollectionException]

  override def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport) = Service {
    val tasks = formedCollectionConversions.toAddCollectionRequest(items) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }.resolve[CollectionException]

  override def getCollections = (persistenceServices.fetchCollections map toCollectionSeq).resolve[CollectionException]

  private[this] def createCollections(
    items: Seq[UnformedItem],
    categories: Seq[String]) = {
    val collections = generateAddCollections(items, categories, Seq.empty)
    candidatesToContactCollection match {
      case Nil => collections
      case candidates =>
        val task = (for {
          s <- fillContacts(candidates)
        } yield s).run
        val contacts = (task map {
          case Answer(c) => c
          case _ => Seq.empty
        }).attemptRun match {
          case -\/(_) => Seq.empty
          case \/-(c) => c
        }
        collections :+ toAddCollectionRequest(contacts, collections.length)
    }
  }

  @tailrec
  private[this] def generateAddCollections(
    items: Seq[UnformedItem],
    categories: Seq[String],
    acc: Seq[AddCollectionRequest]): Seq[AddCollectionRequest] = {
    categories match {
      case Nil => acc
      case h :: t =>
        val insert = generateAddCollection(items, h, acc.length)
        val a = if (insert.cards.length >= minAppsToAdd) acc :+ insert else acc
        generateAddCollections(items, t, a)
    }
  }

  private[this] def generateAddCollection(items: Seq[UnformedItem], category: String, position: Int): AddCollectionRequest = {
    val appsCategory = items.filter(_.category.contains(category)).sortWith(mfIndex(_) < mfIndex(_)).take(numSpaces)
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    AddCollectionRequest(
      position = position,
      name = collectionProcessConfig.namesCategories.getOrElse(category, category.toLowerCase),
      collectionType = CollectionType.apps,
      icon = category.toLowerCase,
      themedColorIndex = themeIndex,
      appsCategory = Some(category),
      sharedCollectionSubscribed = Option(false),
      cards = toAddCardRequestSeq(appsCategory)
    )
  }

  def toAddCollectionRequest(contacts: Seq[Contact], position: Int): AddCollectionRequest = {
    val category = NineCardCategories.contacts
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    AddCollectionRequest(
      position = position,
      name = collectionProcessConfig.namesCategories.getOrElse(category, category.toLowerCase),
      collectionType = CollectionType.contacts,
      icon = category.toLowerCase,
      themedColorIndex = themeIndex,
      appsCategory = None,
      sharedCollectionSubscribed = Option(false),
      cards = toAddCardRequestByContacts(contacts)
    )
  }

  private[this] def candidatesToContactCollection: Seq[Contact] = {
    val task = (for {
      s <- contactsServices.getFavoriteContacts
    } yield s).run
    (task map {
      case Answer(c) => if (c.length >= minAppsToAdd) c.take(numSpaces) else Seq.empty
      case _ => Seq.empty
    }).attemptRun match {
      case -\/(_) => Seq.empty
      case \/-(c) => c
    }
  }

  // TODO Change when ticket is finished (9C-235 - Fetch contacts from several lookup keys)
  private[this] def fillContacts(contacts: Seq[Contact]) = Service {
    val tasks = contacts map (c => contactsServices.findContactByLookupKey(c.lookupKey).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[ContactsServiceException](list.collect { case Answer(contact) => contact }))
  }.resolve[ContactException]

}
