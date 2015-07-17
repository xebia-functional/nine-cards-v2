package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.process.collection.models.{NineCardIntent, Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard, Collection => ServicesCollection}
import play.api.libs.json.Json

trait Conversions {

  def toCollectionSeq(servicesCollectionSeq: Seq[ServicesCollection]): Seq[Collection] =
    servicesCollectionSeq map toCollection

  def toCollection(servicesCollection: ServicesCollection): Collection =
    Collection(
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

  def toCard(servicesCard: ServicesCard): Card =
    Card(
      id = servicesCard.id,
      position = servicesCard.position,
      micros = servicesCard.micros,
      term = servicesCard.term,
      packageName = servicesCard.packageName,
      cardType = servicesCard.cardType,
      intent = Json.parse(servicesCard.intent).as[NineCardIntent],
      imagePath = servicesCard.imagePath,
      starRating = servicesCard.starRating,
      numDownloads = servicesCard.numDownloads,
      notification = servicesCard.notification)
}
