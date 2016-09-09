package com.fortysevendeg.ninecardslauncher.process.collection.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionProcessConfig, Conversions, ImplicitsCollectionException}
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.process.commons.models.PrivateCollection
import com.fortysevendeg.ninecardslauncher.process.commons.types.{ContactsCategory, NineCardCategory, _}
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsServices
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCardRequest, AddCollectionRequest}

import scala.annotation.tailrec
import scalaz.{-\/, \/-}

trait FormedCollectionDependencies {
  val contactsServices: ContactsServices
  val collectionProcessConfig: CollectionProcessConfig
}

trait FormedCollectionConversions
  extends Conversions
  with ImplicitsCollectionException {

  self: FormedCollectionDependencies =>

  def toAddCollectionRequestByFormedCollection(formedCollections: Seq[FormedCollection])(implicit context: ContextSupport): Seq[AddCollectionRequest] =
    formedCollections.zipWithIndex.map(zipped => toAddCollectionRequestByFormedCollection(zipped._1, zipped._2))

  def toAddCollectionRequestByFormedCollection(formedCollection: FormedCollection, position: Int)(implicit context: ContextSupport): AddCollectionRequest = AddCollectionRequest(
    position = position,
    name = formedCollection.name,
    collectionType = formedCollection.collectionType.name,
    icon = formedCollection.icon,
    themedColorIndex = position % numSpaces,
    appsCategory = formedCollection.category map(_.name),
    originalSharedCollectionId = formedCollection.sharedCollectionId,
    sharedCollectionSubscribed = formedCollection.sharedCollectionSubscribed,
    sharedCollectionId = formedCollection.sharedCollectionId,
    cards = toAddCardRequest(formedCollection.items),
    moment = formedCollection.moment map toAddMomentRequest)

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

  def createPrivateCollections(
    apps: Seq[UnformedApp],
    categories: Seq[NineCardCategory],
    minApps: Int): Seq[PrivateCollection] = generatePrivateCollections(apps, categories, Seq.empty)

  @tailrec
  private[this] def generatePrivateCollections(
    items: Seq[UnformedApp],
    categories: Seq[NineCardCategory],
    acc: Seq[PrivateCollection]): Seq[PrivateCollection] = categories match {
      case Nil => acc
      case h :: t =>
        val insert = generatePrivateCollection(items, h, acc.length)
        val a = if (insert.cards.length >= minAppsToAdd) acc :+ insert else acc
        generatePrivateCollections(items, t, a)
    }

  private[this] def generatePrivateCollection(items: Seq[UnformedApp], category: NineCardCategory, position: Int): PrivateCollection = {
    // TODO We should sort the application using an endpoint in the new sever
    val appsByCategory = items.filter(_.category.toAppCategory == category).take(numSpaces)
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    PrivateCollection(
      name = collectionProcessConfig.namesCategories.getOrElse(category, category.getStringResource),
      collectionType = AppsCollectionType,
      icon = category.getStringResource,
      themedColorIndex = themeIndex,
      appsCategory = Some(category),
      cards = appsByCategory map toPrivateCard,
      moment = None
    )
  }

  def createCollections(
    apps: Seq[UnformedApp],
    contacts: Seq[UnformedContact],
    categories: Seq[NineCardCategory],
    minApps: Int): Seq[AddCollectionRequest] = {
    val collections = generateAddCollections(apps, categories, Seq.empty)
    if (contacts.length > minApps) collections :+ toAddCollectionRequestByContact(contacts.take(numSpaces), collections.length)
    else collections
  }

  @tailrec
  private[this] def generateAddCollections(
    items: Seq[UnformedApp],
    categories: Seq[NineCardCategory],
    acc: Seq[AddCollectionRequest]): Seq[AddCollectionRequest] = categories match {
      case Nil => acc
      case h :: t =>
        val insert = generateAddCollection(items, h, acc.length)
        val a = if (insert.cards.length >= minAppsToAdd) acc :+ insert else acc
        generateAddCollections(items, t, a)
    }

  private[this] def generateAddCollection(items: Seq[UnformedApp], category: NineCardCategory, position: Int): AddCollectionRequest = {
    // TODO We should sort the application using an endpoint in the new sever
    val appsCategory = items.filter(_.category.toAppCategory == category).take(numSpaces)
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    AddCollectionRequest(
      position = position,
      name = collectionProcessConfig.namesCategories.getOrElse(category, category.getStringResource),
      collectionType = AppsCollectionType.name,
      icon = category.getIconResource,
      themedColorIndex = themeIndex,
      appsCategory = Some(category.name),
      sharedCollectionSubscribed = Option(false),
      cards = toAddCardRequestSeq(appsCategory),
      moment = None
    )
  }

  def toAddCollectionRequestByContact(contacts: Seq[UnformedContact], position: Int): AddCollectionRequest = {
    val category = ContactsCategory
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    AddCollectionRequest(
      position = position,
      name = collectionProcessConfig.namesCategories.getOrElse(category, category.getStringResource),
      collectionType = ContactsCollectionType.name,
      icon = category.getIconResource,
      themedColorIndex = themeIndex,
      appsCategory = None,
      sharedCollectionSubscribed = Option(false),
      cards = toAddCardRequestByContacts(contacts),
      moment = None
    )
  }

  def fillImageUri(formedCollections: Seq[FormedCollection], apps: Seq[Application])(implicit context: ContextSupport): Seq[FormedCollection] = {
    def fetchPhotoUri(
      extract: => Option[String],
      service: String => TaskService[Option[Contact]]): Option[String] = {
      val maybeContact = extract flatMap { value =>
        val task = (for {
          s <- service(value)
        } yield s).value
        (task map {
          case Xor.Right(r) => r
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
