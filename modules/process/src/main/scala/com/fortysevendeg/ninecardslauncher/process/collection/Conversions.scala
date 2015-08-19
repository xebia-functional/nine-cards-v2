package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.commons.CardType._
import com.fortysevendeg.ninecardslauncher.services.persistence.AddCardRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard, Collection => ServicesCollection}
import play.api.libs.json.Json

trait Conversions {

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
