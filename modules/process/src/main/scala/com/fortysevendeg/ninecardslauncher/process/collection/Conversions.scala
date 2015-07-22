package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.commons.CardType._
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCardRequest, AddCollectionRequest}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard, Collection => ServicesCollection}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import play.api.libs.json.Json
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._

trait Conversions {

  val resourceUtils = new ResourceUtils

  def toCollectionSeq(servicesCollectionSeq: Seq[ServicesCollection]) = servicesCollectionSeq map toCollection

  def toCollection(servicesCollection: ServicesCollection) = Collection(
    id = servicesCollection.id,
    position = servicesCollection.position,
    name = servicesCollection.name,
    collectionType = servicesCollection.collectionType,
    icon = servicesCollection.icon,
    themedColorIndex = servicesCollection.themedColorIndex,
    appsCategory = servicesCollection.appsCategory,
    constrains = servicesCollection.constrains,
    originalSharedCollectionId = servicesCollection.originalSharedCollectionId,
    sharedCollectionId = servicesCollection.originalSharedCollectionId,
    sharedCollectionSubscribed = servicesCollection.sharedCollectionSubscribed,
    cards = servicesCollection.cards map toCard
  )

  def toCard(servicesCard: ServicesCard) = Card(
    id = servicesCard.id,
    position = servicesCard.position,
    micros = servicesCard.micros,
    term = servicesCard.term,
    packageName = servicesCard.packageName,
    cardType = servicesCard.cardType,
    intent = jsonToNineCardIntent(servicesCard.intent),
    imagePath = servicesCard.imagePath,
    starRating = servicesCard.starRating,
    numDownloads = servicesCard.numDownloads,
    notification = servicesCard.notification)

  def toAddCardRequestSeq(items: Seq[UnformedItem]): Seq[AddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestFromUnformedItems(zipped._1, zipped._2))

  def toAddCardRequestFromUnformedItems(item: UnformedItem, position: Int) = AddCardRequest(
    position = position,
    term = item.name,
    packageName = Option(item.packageName),
    cardType = app,
    intent = nineCardIntentToJson(toNineCardIntent(item)),
    imagePath = item.imagePath
  )

  def toAddCollectionRequestFromFormedCollections(formedCollections: Seq[FormedCollection])(implicit context: ContextSupport): Seq[AddCollectionRequest] =
    formedCollections.zipWithIndex.map(zipped => toAddCollectionRequest(zipped._1, zipped._2))

  def toAddCollectionRequest(formedCollection: FormedCollection, position: Int)(implicit context: ContextSupport) = AddCollectionRequest(
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
    cards = toAddCardRequestFromFormedItems(formedCollection.items)
  )

  def toAddCardRequestFromFormedItems(items: Seq[FormedItem])(implicit context: ContextSupport) =
    items.zipWithIndex.map(zipped => toAddCardRequest(zipped._1, zipped._2))

  def toAddCardRequest(item: FormedItem, position: Int)(implicit context: ContextSupport): AddCardRequest = {
    val nineCardIntent = jsonToNineCardIntent(item.intent)
    val path = (item.itemType match {
      case `app` =>
        for {
          packageName <- nineCardIntent.extractPackageName()
          className <- nineCardIntent.extractClassName()
        } yield resourceUtils.getPathPackage(packageName, className)
      case _ => None
    }) getOrElse "" // TODO We should use a default image
    AddCardRequest(
      position = position,
      term = item.title,
      packageName = nineCardIntent.extractPackageName(),
      cardType = item.itemType,
      intent = item.intent,
      imagePath = path
    )
  }

  def toNineCardIntent(item: UnformedItem) = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(item.packageName),
      class_name = Option(item.className)))
    intent.setAction(openApp)
    intent.setClassName(item.packageName, item.className)
    intent
  }

  private[this] def jsonToNineCardIntent(json: String) = Json.parse(json).as[NineCardIntent]

  private[this] def nineCardIntentToJson(intent: NineCardIntent) = Json.toJson(intent).toString()

}
