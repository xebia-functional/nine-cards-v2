package com.fortysevendeg.ninecardslauncher.process.collection.impl

import java.io.File

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection.{ImplicitsCollectionException, Conversions, CollectionProcessConfig, ContactException}
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{UnformedItem, FormedCollection, FormedItem, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.collection.utils.NineCardAppUtils._
import com.fortysevendeg.ninecardslauncher.process.commons.CardType._
import com.fortysevendeg.ninecardslauncher.process.commons.{NineCardCategories, CollectionType}
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact
import com.fortysevendeg.ninecardslauncher.services.contacts.{ImplicitsContactsServiceExceptions, ContactsServiceException, ContactsServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCardRequest, AddCollectionRequest}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import play.api.libs.json.Json
import rapture.core.Answer

import scala.annotation.tailrec
import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

trait FormedCollectionDependencies {
  val resourceUtils: ResourceUtils
  val contactsServices: ContactsServices
  val collectionProcessConfig: CollectionProcessConfig
}

trait FormedCollectionConversions
  extends Conversions
  with ImplicitsCollectionException
  with ImplicitsContactsServiceExceptions {

  self: FormedCollectionDependencies =>

  def toAddCollectionRequestByFormedCollection(formedCollections: Seq[FormedCollection])(implicit context: ContextSupport): Seq[AddCollectionRequest] =
    formedCollections.zipWithIndex.map(zipped => toAddCollectionRequestByFormedCollection(zipped._1, zipped._2))

  def toAddCollectionRequestByFormedCollection(formedCollection: FormedCollection, position: Int)(implicit context: ContextSupport) = AddCollectionRequest(
    position = position,
    name = formedCollection.name,
    collectionType = formedCollection.collectionType,
    icon = formedCollection.icon,
    themedColorIndex = position % numSpaces,
    appsCategory = formedCollection.category,
    constrains = None,
    originalSharedCollectionId = formedCollection.sharedCollectionId,
    sharedCollectionSubscribed = formedCollection.sharedCollectionSubscribed,
    sharedCollectionId = formedCollection.sharedCollectionId,
    cards = toAddCardRequest(formedCollection.items)
  )

  def toAddCardRequest(items: Seq[FormedItem])(implicit context: ContextSupport): Seq[AddCardRequest] =
    items.zipWithIndex.map(zipped => toAddCardRequest(zipped._1, zipped._2))

  def toAddCardRequest(item: FormedItem, position: Int)(implicit context: ContextSupport): AddCardRequest = {

    def fetchPhotoUri(
      extract: => Option[String],
      service: String => ServiceDef2[Option[Contact], ContactsServiceException]): Option[String] = {
      val maybeContact = extract flatMap { value =>
        val task = (for {
          s <- service(value)
        } yield s).run
        (task map {
          case Answer(r) => r
          case _ => None
        }).attemptRun match {
          case -\/(f) => None
          case \/-(f) => f
        }
      }
      maybeContact map (_.photoUri)
    }

    val nineCardIntent = jsonToNineCardIntent(item.intent)
    val path = (item.itemType match {
      case `app` =>
        for {
          packageName <- nineCardIntent.extractPackageName()
          className <- nineCardIntent.extractClassName()
        } yield {
          val pathWithClassName = resourceUtils.getPathPackage(packageName, className)
          // If the path using ClassName don't exist, we use a path using only packagename
          if (new File(pathWithClassName).exists) pathWithClassName else  resourceUtils.getPath(packageName)
        }
      case `phone` | `sms` =>
        fetchPhotoUri(nineCardIntent.extractPhone(), contactsServices.fetchContactByPhoneNumber)
      case `email` =>
        fetchPhotoUri(nineCardIntent.extractEmail(), contactsServices.fetchContactByEmail)
      case _ => None
    }) getOrElse "" // UI will create the default image
    AddCardRequest(
      position = position,
      term = item.title,
      packageName = nineCardIntent.extractPackageName(),
      cardType = item.itemType,
      intent = item.intent,
      imagePath = path
    )
  }

  def createCollections(
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
        collections :+ toAddCollectionRequestByContact(contacts, collections.length)
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
    val appsCategory = items.filter(_.category.contains(category)).sortWith(mfIndex(_) > mfIndex(_)).take(numSpaces)
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

  def toAddCollectionRequestByContact(contacts: Seq[Contact], position: Int): AddCollectionRequest = {
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

  private[this] def jsonToNineCardIntent(json: String) = Json.parse(json).as[NineCardIntent]

}
