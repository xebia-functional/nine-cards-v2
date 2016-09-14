package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.commons.CommonConversions
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection, NineCardIntent, PrivateCard}
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppCardType
import com.fortysevendeg.ninecardslauncher.services.api.CategorizedPackage
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{App => ServicesApp, Card => ServicesCard, Collection => ServicesCollection}
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCardRequest => ServicesAddCardRequest, AddCollectionRequest => ServicesAddCollectionRequest, UpdateCardRequest => ServicesUpdateCardRequest, UpdateCardsRequest => ServicesUpdateCardsRequest, UpdateCollectionRequest => ServicesUpdateCollectionRequest, UpdateCollectionsRequest => ServicesUpdateCollectionsRequest, _}

trait Conversions extends CommonConversions {

  def toCollectionSeq(servicesCollectionSeq: Seq[ServicesCollection]) = servicesCollectionSeq map toCollection

  def toAddCollectionRequest(addCollectionRequest: AddCollectionRequest, position: Int): ServicesAddCollectionRequest = ServicesAddCollectionRequest(
    position = position,
    name = addCollectionRequest.name,
    collectionType = addCollectionRequest.collectionType.name,
    icon = addCollectionRequest.icon,
    themedColorIndex = addCollectionRequest.themedColorIndex,
    appsCategory = addCollectionRequest.appsCategory map(_.name),
    originalSharedCollectionId = addCollectionRequest.originalSharedCollectionId,
    sharedCollectionId = addCollectionRequest.sharedCollectionId,
    sharedCollectionSubscribed = addCollectionRequest.sharedCollectionSubscribed,
    cards = addCollectionRequest.cards.zipWithIndex.map {
      case (card, index) => toAddCardRequest(card, index)
    },
    moment = addCollectionRequest.moment map (moment => toAddMomentRequest(None, moment)))

  def toFindCollectionByIdRequest(collectionId: Int): FindCollectionByIdRequest = FindCollectionByIdRequest(
    id = collectionId)

  def toServicesUpdateCollectionRequest(collection: Collection): ServicesUpdateCollectionRequest = ServicesUpdateCollectionRequest(
    id = collection.id,
    position = collection.position,
    name = collection.name,
    collectionType = collection.collectionType.name,
    icon = collection.icon,
    themedColorIndex = collection.themedColorIndex,
    appsCategory = collection.appsCategory map(_.name),
    originalSharedCollectionId = collection.originalSharedCollectionId,
    sharedCollectionId = collection.sharedCollectionId,
    sharedCollectionSubscribed = Option(collection.sharedCollectionSubscribed),
    cards = collection.cards map toServicesCard)

  def toServicesUpdateCollectionsRequest(collections: Seq[Collection]): ServicesUpdateCollectionsRequest =
    ServicesUpdateCollectionsRequest(collections map toServicesUpdateCollectionRequest)

  def toUpdatedCollection(collection: Collection, editCollectionRequest: EditCollectionRequest): Collection =  Collection(
    id = collection.id,
    position = collection.position,
    name = editCollectionRequest.name,
    collectionType = collection.collectionType,
    icon = editCollectionRequest.icon,
    themedColorIndex = editCollectionRequest.themedColorIndex,
    appsCategory = editCollectionRequest.appsCategory,
    originalSharedCollectionId = collection.originalSharedCollectionId,
    sharedCollectionId = collection.sharedCollectionId,
    sharedCollectionSubscribed = collection.sharedCollectionSubscribed,
    cards = collection.cards,
    moment = collection.moment)

  def toUpdatedSharedCollection(collection: Collection, sharedCollectionId: String): Collection =  Collection(
    id = collection.id,
    position = collection.position,
    name = collection.name,
    collectionType = collection.collectionType,
    icon = collection.icon,
    themedColorIndex = collection.themedColorIndex,
    appsCategory = collection.appsCategory,
    originalSharedCollectionId = collection.originalSharedCollectionId,
    sharedCollectionId = Some(sharedCollectionId),
    sharedCollectionSubscribed = collection.sharedCollectionSubscribed,
    cards = collection.cards,
    moment = collection.moment)

  def toUpdatedSharedCollection(collection: Collection, originalSharedCollectionId: Option[String]): Collection =  Collection(
    id = collection.id,
    position = collection.position,
    name = collection.name,
    collectionType = collection.collectionType,
    icon = collection.icon,
    themedColorIndex = collection.themedColorIndex,
    appsCategory = collection.appsCategory,
    originalSharedCollectionId = originalSharedCollectionId,
    sharedCollectionId = collection.sharedCollectionId,
    sharedCollectionSubscribed = collection.sharedCollectionSubscribed,
    cards = collection.cards,
    moment = collection.moment)

  def toFetchCollectionByPositionRequest(pos: Int): FetchCollectionByPositionRequest = FetchCollectionByPositionRequest(
    position = pos)

  def toCardSeq(servicesCardSeq: Seq[ServicesCard]): Seq[Card] = servicesCardSeq map toCard

  def toServicesCard(card: Card): ServicesCard = ServicesCard(
    id = card.id,
    position = card.position,
    term = card.term,
    packageName = card.packageName,
    cardType = card.cardType.name,
    intent = nineCardIntentToJson(card.intent),
    imagePath = card.imagePath,
    notification = card.notification)

  def toAddCardRequestSeq(items: Seq[UnformedApp]): Seq[ServicesAddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestFromUnformedItems(zipped._1, zipped._2))

  def toAddCardRequestFromUnformedItems(item: UnformedApp, position: Int): ServicesAddCardRequest = ServicesAddCardRequest(
    position = position,
    term = item.name,
    packageName = Option(item.packageName),
    cardType = AppCardType.name,
    intent = nineCardIntentToJson(toNineCardIntent(item)),
    imagePath = item.imagePath)

  def toFetchCardsByCollectionRequest(collectionRequestId: Int): FetchCardsByCollectionRequest = FetchCardsByCollectionRequest(
    collectionId = collectionRequestId)

  def toAddCardRequest(addCardRequest: AddCardRequest, position: Int): ServicesAddCardRequest = ServicesAddCardRequest (
    collectionId = None,
    position = position,
    term = addCardRequest.term,
    packageName = addCardRequest.packageName,
    cardType = addCardRequest.cardType.name,
    intent = nineCardIntentToJson(addCardRequest.intent),
    imagePath = addCardRequest.imagePath)

  def toAddCardRequest(collectionId: Int, addCardRequest: AddCardRequest, position: Int): ServicesAddCardRequest = ServicesAddCardRequest (
    collectionId = Option(collectionId),
    position = position,
    term = addCardRequest.term,
    packageName = addCardRequest.packageName,
    cardType = addCardRequest.cardType.name,
    intent = nineCardIntentToJson(addCardRequest.intent),
    imagePath = addCardRequest.imagePath)

  def toAddCardRequest(collectionId: Int, app: ServicesApp, position: Int): ServicesAddCardRequest = ServicesAddCardRequest (
    collectionId = Option(collectionId),
    position = position,
    term = app.name,
    packageName = Option(app.packageName),
    cardType = AppCardType.name,
    intent = nineCardIntentToJson(toNineCardIntent(app)),
    imagePath = app.imagePath)

  def toAddCardRequest(collectionId: Int, categorizedPackage: CategorizedPackage, position: Int): ServicesAddCardRequest = ServicesAddCardRequest (
    collectionId = Option(collectionId),
    position = position,
    term = categorizedPackage.packageName, // TODO - Fix as part of https://github.com/47deg/nine-cards-v2/issues/653
    packageName = Option(categorizedPackage.packageName),
    cardType = AppCardType.name,
    intent = nineCardIntentToJson(packageToNineCardIntent(categorizedPackage.packageName)),
    imagePath = "")

  def toFindCardByIdRequest(cardId: Int): FindCardByIdRequest = FindCardByIdRequest(
    id = cardId)

  def toServicesUpdateCardsRequest(cards: Seq[Card]): ServicesUpdateCardsRequest =
    ServicesUpdateCardsRequest(cards map toServicesUpdateCardRequest)

  def toServicesUpdateCardRequest(card: Card): ServicesUpdateCardRequest = ServicesUpdateCardRequest(
    id = card.id,
    position = card.position,
    term = card.term,
    packageName = card.packageName,
    cardType = card.cardType.name,
    intent = nineCardIntentToJson(card.intent),
    imagePath = card.imagePath,
    notification = card.notification)

  def toInstalledApp(cards: Seq[ServicesCard], app: Application)(implicit contextSupport: ContextSupport): Seq[ServicesCard] = {
    val intent = toNineCardIntent(app)
    cards map (_.copy(
      term = app.name,
      cardType = AppCardType.name,
      intent = nineCardIntentToJson(intent)
    ))
  }

  def toAddCardRequestByContacts(items: Seq[UnformedContact]): Seq[ServicesAddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestByContact(zipped._1, zipped._2))

  def toAddCardRequestByContact(item: UnformedContact, position: Int): ServicesAddCardRequest = {
    val (intent: NineCardIntent, cardType: String) = toNineCardIntent(item)
    ServicesAddCardRequest(
      position = position,
      term = item.name,
      packageName = None,
      cardType = cardType,
      intent = nineCardIntentToJson(intent),
      imagePath = item.photoUri)
  }

  def toPrivateCard(unformedApp: UnformedApp): PrivateCard =
    PrivateCard(
      term = unformedApp.name,
      packageName = Some(unformedApp.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(unformedApp),
      imagePath = unformedApp.imagePath)

}
