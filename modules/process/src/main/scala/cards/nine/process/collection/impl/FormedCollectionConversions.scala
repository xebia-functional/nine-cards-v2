package cards.nine.process.collection.impl

import cards.nine.commons.contexts.ContextSupport
import cards.nine.models._
import cards.nine.models.types.Spaces._
import cards.nine.models.types._
import cards.nine.process.collection.ImplicitsCollectionException
import cards.nine.services.contacts.ContactsServices

import scala.annotation.tailrec

trait FormedCollectionDependencies {
  val contactsServices: ContactsServices
  val collectionProcessConfig: CollectionProcessConfig
}

trait FormedCollectionConversions
  extends Conversions
  with NineCardsIntentConversions
  with ImplicitsCollectionException {

  self: FormedCollectionDependencies =>
//
//  def toCollectionDataByFormedCollection(formedCollections: Seq[CollectionData])(implicit context: ContextSupport): Seq[CollectionData] =
//    formedCollections.zipWithIndex.map(zipped => toCollectionDataByFormedCollection(zipped._1, zipped._2))
//
//  def toCollectionDataByFormedCollection(formedCollection: CollectionData, position: Int)(implicit context: ContextSupport): CollectionData = CollectionData(
//    position = position,
//    name = formedCollection.name,
//    collectionType = formedCollection.collectionType,
//    icon = formedCollection.icon,
//    themedColorIndex = position % numSpaces,
//    appsCategory = formedCollection.appsCategory,
//    originalSharedCollectionId = formedCollection.originalSharedCollectionId,
//    sharedCollectionSubscribed = formedCollection.sharedCollectionSubscribed,
//    sharedCollectionId = formedCollection.sharedCollectionId,
//    cards = formedCollection.cards,
//    moment = formedCollection.moment,
//    publicCollectionStatus = NotPublished)

//  def toCardData(items: Seq[FormedItem])(implicit context: ContextSupport): Seq[CardData] =
//    items.zipWithIndex.map(zipped => toCardData(zipped._1, zipped._2))
//
//  def toCardData(item: FormedItem, position: Int)(implicit context: ContextSupport): CardData = {
//    val nineCardIntent = jsonToNineCardIntent(item.intent)
//    CardData(
//      position = position,
//      term = item.title,
//      packageName = nineCardIntent.extractPackageName(),
//      cardType = CardType(item.itemType),
//      intent = nineCardIntent,
//      imagePath = item.uriImage)
//  }

//  def toMomentData(moment: FormedMoment): MomentData =
//    MomentData(
//      collectionId = moment.collectionId,
//      timeslot = moment.timeslot,
//      wifi = moment.wifi,
//      headphone = moment.headphone,
//      momentType = moment.momentType,
//      widgets = moment.widgets)

  def createPrivateCollections(
    apps: Seq[ApplicationData],
    categories: Seq[NineCardsCategory],
    minApps: Int): Seq[CollectionData] = generatePrivateCollections(apps, categories, Seq.empty)

  @tailrec
  private[this] def generatePrivateCollections(
    items: Seq[ApplicationData],
    categories: Seq[NineCardsCategory],
    acc: Seq[CollectionData]): Seq[CollectionData] = categories match {
      case Nil => acc
      case h :: t =>
        val insert = generatePrivateCollection(items, h, acc.length)
        val a = if (insert.cards.length >= minAppsToAdd) acc :+ insert else acc
        generatePrivateCollections(items, t, a)
    }

  private[this] def generatePrivateCollection(items: Seq[ApplicationData], category: NineCardsCategory, position: Int): CollectionData = {
    // TODO We should sort the application using an endpoint in the new sever
    val appsByCategory = items.filter(_.category.toAppCategory == category).take(numSpaces)
    val themeIndex = if (position >= numSpaces) position % numSpaces else position
    CollectionData (
      position = position,
      name = collectionProcessConfig.namesCategories.getOrElse(category, category.getStringResource),
      collectionType = AppsCollectionType,
      icon = category.getStringResource,
      themedColorIndex = themeIndex,
      appsCategory = Some(category),
      cards = appsByCategory map toCardData,
      moment = None,
      originalSharedCollectionId = None,
      sharedCollectionId = None,
      sharedCollectionSubscribed = false,
      publicCollectionStatus = NotPublished)
  }

  def toCardData(application: ApplicationData): CardData =
    CardData(
      term = application.name,
      packageName = Some(application.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(application),
      imagePath = None)

  def adaptCardsToAppsInstalled(collections: Seq[CollectionData], apps: Seq[ApplicationData]): Seq[CollectionData] =
    collections map { c =>
      val cardsWithPath = c.cards map { card =>
        val nineCardIntent = card.intent

        // We need adapt items to apps installed in cell phone
        val cardAdapted: CardData = card.cardType match {
          case AppCardType | RecommendedAppCardType =>
            (for {
              packageName <- nineCardIntent.extractPackageName()
              className <- nineCardIntent.extractClassName()
            } yield {
              val maybeAppInstalled = apps find (_.packageName == packageName)
              maybeAppInstalled map { appInstalled =>
                val classChanged = !(appInstalled.className == className)
                if (classChanged) {
                  card.copy(intent = toNineCardIntent(appInstalled), cardType = AppCardType)
                } else {
                  card.copy(cardType = AppCardType)
                }
              } getOrElse card.copy(cardType = NoInstalledAppCardType)
            }) getOrElse card.copy(cardType = NoInstalledAppCardType)
          case _ => card
        }
        cardAdapted
      }
      c.copy(cards = cardsWithPath)
    }

}
