package cards.nine.process.collection.impl

import cards.nine.models.{Collection, ApplicationData}
import cards.nine.models.types.Spaces._
import cards.nine.models.types._
import cards.nine.process.collection.models._
import cards.nine.process.collection.{CollectionProcessConfig, Conversions, ImplicitsCollectionException}
import cards.nine.process.commons.models.PrivateCollection
import cards.nine.services.contacts.ContactsServices

import scala.annotation.tailrec

trait FormedCollectionDependencies {
  val contactsServices: ContactsServices
  val collectionProcessConfig: CollectionProcessConfig
}

trait FormedCollectionConversions
  extends Conversions
  with ImplicitsCollectionException {

  self: FormedCollectionDependencies =>

//  def toAddCollectionRequestByFormedCollection(formedCollections: Seq[FormedCollection])(implicit context: ContextSupport): Seq[AddCollectionRequest] =
//    formedCollections.zipWithIndex.map(zipped => toAddCollectionRequestByFormedCollection(zipped._1, zipped._2))
//
//  def toAddCollectionRequestByFormedCollection(formedCollection: FormedCollection, position: Int)(implicit context: ContextSupport): AddCollectionRequest = AddCollectionRequest(
//    position = position,
//    name = formedCollection.name,
//    collectionType = formedCollection.collectionType.name,
//    icon = formedCollection.icon,
//    themedColorIndex = position % numSpaces,
//    appsCategory = formedCollection.category map(_.name),
//    originalSharedCollectionId = formedCollection.originalSharedCollectionId,
//    sharedCollectionSubscribed = formedCollection.sharedCollectionSubscribed,
//    sharedCollectionId = formedCollection.sharedCollectionId,
//    cards = toAddCardRequest(formedCollection.items),
//    moment = formedCollection.moment map toAddMomentRequest)
//
//  def toAddCardRequest(items: Seq[FormedItem])(implicit context: ContextSupport): Seq[AddCardRequest] =
//    items.zipWithIndex.map(zipped => toAddCardRequest(zipped._1, zipped._2))
//
//  def toAddCardRequest(item: FormedItem, position: Int)(implicit context: ContextSupport): AddCardRequest = {
//    val nineCardIntent = jsonToNineCardIntent(item.intent)
//    AddCardRequest(
//      position = position,
//      term = item.title,
//      packageName = nineCardIntent.extractPackageName(),
//      cardType = item.itemType,
//      intent = item.intent,
//      imagePath = item.uriImage
//    )
//  }

  def createPrivateCollections(
    apps: Seq[ApplicationData],
    categories: Seq[NineCardsCategory],
    minApps: Int): Seq[Collection] = generatePrivateCollections(apps, categories, Seq.empty)

  @tailrec
  private[this] def generatePrivateCollections(
    items: Seq[ApplicationData],
    categories: Seq[NineCardsCategory],
    acc: Seq[Collection]): Seq[Collection] = categories match {
      case Nil => acc
      case h :: t =>
        val insert = generatePrivateCollection(items, h, acc.length)
        val a = if (insert.cards.length >= minAppsToAdd) acc :+ insert else acc
        generatePrivateCollections(items, t, a)
    }

  private[this] def generatePrivateCollection(items: Seq[ApplicationData], category: NineCardsCategory, position: Int): Collection = {
    // TODO We should sort the application using an endpoint in the new sever
    val appsByCategory = items.filter(_.category.toAppCategory == category).take(numSpaces)
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    Collection(
      name = collectionProcessConfig.namesCategories.getOrElse(category, category.getStringResource),
      collectionType = AppsCollectionType,
      icon = category.getStringResource,
      themedColorIndex = themeIndex,
      appsCategory = Some(category),
      cards = appsByCategory map toPrivateCard,
      moment = None
    )
  }

  def adaptCardsToAppsInstalled(formedCollections: Seq[FormedCollection], apps: Seq[ApplicationData]): Seq[FormedCollection] =
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
        itemAdapted
      }
      fc.copy(items = itemsWithPath)
    }

}
