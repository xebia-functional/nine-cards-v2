package com.fortysevendeg.ninecardslauncher.process.commons

import com.fortysevendeg.ninecardslauncher.process.commons.models._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{NineCardCategory, _}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard, Collection => ServicesCollection}

trait CommonConversions extends NineCardIntentConversions {

  def toCollection(servicesCollection: ServicesCollection) = Collection(
    id = servicesCollection.id,
    position = servicesCollection.position,
    name = servicesCollection.name,
    collectionType = CollectionType(servicesCollection.collectionType),
    icon = servicesCollection.icon,
    themedColorIndex = servicesCollection.themedColorIndex,
    appsCategory = servicesCollection.appsCategory map (NineCardCategory(_)),
    constrains = servicesCollection.constrains,
    originalSharedCollectionId = servicesCollection.originalSharedCollectionId,
    sharedCollectionId = servicesCollection.sharedCollectionId,
    sharedCollectionSubscribed = servicesCollection.sharedCollectionSubscribed,
    cards = servicesCollection.cards map toCard)

  def toCard(servicesCard: ServicesCard) = Card(
    id = servicesCard.id,
    position = servicesCard.position,
    micros = servicesCard.micros,
    term = servicesCard.term,
    packageName = servicesCard.packageName,
    cardType = CardType(servicesCard.cardType),
    intent = jsonToNineCardIntent(servicesCard.intent),
    imagePath = servicesCard.imagePath,
    starRating = servicesCard.starRating,
    numDownloads = servicesCard.numDownloads,
    notification = servicesCard.notification)

}
