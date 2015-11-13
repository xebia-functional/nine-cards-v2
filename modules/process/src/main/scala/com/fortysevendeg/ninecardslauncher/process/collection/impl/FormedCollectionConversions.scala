package com.fortysevendeg.ninecardslauncher.process.collection.impl

import java.io.File

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionProcessConfig, Conversions, ImplicitsCollectionException}
import com.fortysevendeg.ninecardslauncher.process.commons.types.{ContactsCategory, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.types._
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ContactsServices, ImplicitsContactsServiceExceptions}
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCardRequest, AddCollectionRequest}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import rapture.core.Answer

import scala.annotation.tailrec
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
    collectionType = formedCollection.collectionType.name,
    icon = formedCollection.icon,
    themedColorIndex = position % numSpaces,
    appsCategory = formedCollection.category map(_.name),
    constrains = None,
    originalSharedCollectionId = formedCollection.sharedCollectionId,
    sharedCollectionSubscribed = formedCollection.sharedCollectionSubscribed,
    sharedCollectionId = formedCollection.sharedCollectionId,
    cards = toAddCardRequest(formedCollection.items)
  )

  def toAddCardRequest(items: Seq[FormedItem])(implicit context: ContextSupport): Seq[AddCardRequest] =
    items.zipWithIndex.map(zipped => toAddCardRequest(zipped._1, zipped._2))

  def toAddCardRequest(item: FormedItem, position: Int)(implicit context: ContextSupport): AddCardRequest = {
    val nineCardIntent = jsonToNineCardIntent(item.intent)
    AddCardRequest(
      position = position,
      term = item.title,
      packageName = nineCardIntent.extractPackageName(),
      cardType = item.itemType,
      intent = item.intent,
      imagePath = item.uriImage getOrElse "" // UI will create the default image
    )
  }

  def createCollections(
    apps: Seq[UnformedApp],
    contacts: Seq[UnformedContact],
    categories: Seq[NineCardCategory]) = {
    val collections = generateAddCollections(apps, categories, Seq.empty)
    if (contacts.length > minAppsToAdd) collections :+ toAddCollectionRequestByContact(contacts.take(numSpaces), collections.length)
    else collections
  }

  @tailrec
  private[this] def generateAddCollections(
    items: Seq[UnformedApp],
    categories: Seq[NineCardCategory],
    acc: Seq[AddCollectionRequest]): Seq[AddCollectionRequest] = {
    categories match {
      case Nil => acc
      case h :: t =>
        val insert = generateAddCollection(items, h, acc.length)
        val a = if (insert.cards.length >= minAppsToAdd) acc :+ insert else acc
        generateAddCollections(items, t, a)
    }
  }

  private[this] def generateAddCollection(items: Seq[UnformedApp], category: NineCardCategory, position: Int): AddCollectionRequest = {
    // TODO We should sort the application using an endpoint in the new sever
    val appsCategory = items.filter(_.category.name.contains(category.name)).take(numSpaces)
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    AddCollectionRequest(
      position = position,
      name = collectionProcessConfig.namesCategories.getOrElse(category, category.name.toLowerCase),
      collectionType = AppsCollectionType.name,
      icon = category.name.toLowerCase,
      themedColorIndex = themeIndex,
      appsCategory = Some(category.name),
      sharedCollectionSubscribed = Option(false),
      cards = toAddCardRequestSeq(appsCategory)
    )
  }

  def toAddCollectionRequestByContact(contacts: Seq[UnformedContact], position: Int): AddCollectionRequest = {
    val category = ContactsCategory
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    AddCollectionRequest(
      position = position,
      name = collectionProcessConfig.namesCategories.getOrElse(category, category.name.toLowerCase),
      collectionType = ContactsCollectionType.name,
      icon = category.name.toLowerCase,
      themedColorIndex = themeIndex,
      appsCategory = None,
      sharedCollectionSubscribed = Option(false),
      cards = toAddCardRequestByContacts(contacts)
    )
  }

  def fillImageUri(formedCollections: Seq[FormedCollection], apps: Seq[Application])(implicit context: ContextSupport): Seq[FormedCollection] = {
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
    formedCollections map { fc =>
      val itemsWithPath = fc.items map { item =>
        val nineCardIntent = jsonToNineCardIntent(item.intent)

        // We need adapt items to apps installed in cell phone
        val itemAdapted: FormedItem = CardType(item.itemType) match {
          case AppCardType | RecommendedAppCardType =>
            (for {
              packageName <- nineCardIntent.extractPackageName()
              className <- nineCardIntent.extractClassName()
            } yield {
              val maybeAppInstalled = apps find (_.packageName == packageName)
              maybeAppInstalled map { appInstalled =>
                val classChanged = !(appInstalled.className == className)
                if (classChanged) {
                  val json = nineCardIntentToJson(toNineCardIntent(appInstalled))
                  item.copy(intent = json, itemType = AppCardType.name)
                } else {
                  item.copy(itemType = AppCardType.name)
                }
              } getOrElse item.copy(itemType = NoInstalledAppCardType.name)
            }) getOrElse item.copy(itemType = NoInstalledAppCardType.name)
          case _ => item
        }

        val nineCardIntentAdapted = jsonToNineCardIntent(itemAdapted.intent)

        val path = CardType(itemAdapted.itemType) match {
          case AppCardType =>
            for {
              packageName <- nineCardIntentAdapted.extractPackageName()
              className <- nineCardIntentAdapted.extractClassName()
            } yield {
              val pathWithClassName = resourceUtils.getPathPackage(packageName, className)
              // If the path using ClassName don't exist, we use a path using only packagename
              if (new File(pathWithClassName).exists) pathWithClassName else resourceUtils.getPath(packageName)
            }
          case PhoneCardType | SmsCardType =>
            fetchPhotoUri(nineCardIntentAdapted.extractPhone(), contactsServices.fetchContactByPhoneNumber)
          case EmailCardType =>
            fetchPhotoUri(nineCardIntentAdapted.extractEmail(), contactsServices.fetchContactByEmail)
          case _ => None
        }
        itemAdapted.copy(uriImage = path)
      }
      fc.copy(items = itemsWithPath)
    }
  }

}
