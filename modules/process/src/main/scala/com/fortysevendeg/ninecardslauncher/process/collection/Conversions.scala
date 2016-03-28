package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.commons.CommonConversions
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection, NineCardIntent, PrivateCard}
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppCardType
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard, Collection => ServicesCollection}
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCardRequest => ServicesAddCardRequest, AddCollectionRequest => ServicesAddCollectionRequest, UpdateCardRequest => ServicesUpdateCardRequest, UpdateCollectionRequest => ServicesUpdateCollectionRequest, _}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils

trait Conversions extends CommonConversions {

  val resourceUtils = new ResourceUtils

  def toCollectionSeq(servicesCollectionSeq: Seq[ServicesCollection]) = servicesCollectionSeq map toCollection

  def toAddCollectionRequest(addCollectionRequest: AddCollectionRequest, position: Int) = ServicesAddCollectionRequest(
    position = position,
    name = addCollectionRequest.name,
    collectionType = addCollectionRequest.collectionType.name,
    icon = addCollectionRequest.icon,
    themedColorIndex = addCollectionRequest.themedColorIndex,
    appsCategory = addCollectionRequest.appsCategory map(_.name),
    constrains = addCollectionRequest.constrains,
    originalSharedCollectionId = addCollectionRequest.originalSharedCollectionId,
    sharedCollectionId = addCollectionRequest.sharedCollectionId,
    sharedCollectionSubscribed = addCollectionRequest.sharedCollectionSubscribed,
    cards = Seq())

  def toFindCollectionByIdRequest(collectionId: Int) = FindCollectionByIdRequest(
    id = collectionId)

  def toServicesUpdateCollectionRequest(collection: Collection) = ServicesUpdateCollectionRequest(
    id = collection.id,
    position = collection.position,
    name = collection.name,
    collectionType = collection.collectionType.name,
    icon = collection.icon,
    themedColorIndex = collection.themedColorIndex,
    appsCategory = collection.appsCategory map(_.name),
    constrains = collection.constrains,
    originalSharedCollectionId = collection.originalSharedCollectionId,
    sharedCollectionId = collection.sharedCollectionId,
    sharedCollectionSubscribed = Option(collection.sharedCollectionSubscribed),
    cards = collection.cards map toServicesCard)

  def toUpdatedCollection(collection: Collection, editCollectionRequest: EditCollectionRequest) =  Collection(
    id = collection.id,
    position = collection.position,
    name = editCollectionRequest.name,
    collectionType = collection.collectionType,
    icon = editCollectionRequest.icon,
    themedColorIndex = editCollectionRequest.themedColorIndex,
    appsCategory = editCollectionRequest.appsCategory,
    constrains = collection.constrains,
    originalSharedCollectionId = collection.originalSharedCollectionId,
    sharedCollectionId = collection.sharedCollectionId,
    sharedCollectionSubscribed = collection.sharedCollectionSubscribed,
    cards = collection.cards)

  def toFetchCollectionByPositionRequest(pos: Int) = FetchCollectionByPositionRequest(
    position = pos)

  def toCardSeq(servicesCardSeq: Seq[ServicesCard]) = servicesCardSeq map toCard

  def toServicesCard(card: Card) = ServicesCard(
    id = card.id,
    position = card.position,
    micros = card.micros,
    term = card.term,
    packageName = card.packageName,
    cardType = card.cardType.name,
    intent = nineCardIntentToJson(card.intent),
    imagePath = card.imagePath,
    starRating = card.starRating,
    numDownloads = card.numDownloads,
    notification = card.notification)

  def toAddCardRequestSeq(items: Seq[UnformedApp]): Seq[ServicesAddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestFromUnformedItems(zipped._1, zipped._2))

  def toAddCardRequestFromUnformedItems(item: UnformedApp, position: Int) = ServicesAddCardRequest(
    position = position,
    term = item.name,
    packageName = Option(item.packageName),
    cardType = AppCardType.name,
    intent = nineCardIntentToJson(toNineCardIntent(item)),
    imagePath = item.imagePath)

  def toFetchCardsByCollectionRequest(collectionRequestId: Int) = FetchCardsByCollectionRequest(
    collectionId = collectionRequestId)

  def toAddCardRequest(collectionId: Int, addCardRequest: AddCardRequest, position: Int) = ServicesAddCardRequest (
    collectionId = Option(collectionId),
    position = position,
    term = addCardRequest.term,
    packageName = addCardRequest.packageName,
    cardType = addCardRequest.cardType.name,
    intent = nineCardIntentToJson(addCardRequest.intent),
    imagePath = addCardRequest.imagePath)

  def toFindCardByIdRequest(cardId: Int) = FindCardByIdRequest(
    id = cardId)

  def toServicesUpdateCardRequest(card: Card) = ServicesUpdateCardRequest(
    id = card.id,
    position = card.position,
    micros = card.micros,
    term = card.term,
    packageName = card.packageName,
    cardType = card.cardType.name,
    intent = nineCardIntentToJson(card.intent),
    imagePath = card.imagePath,
    starRating = card.starRating,
    numDownloads = card.numDownloads,
    notification = card.notification)

  def toInstalledApp(cards: Seq[ServicesCard], app: Application)(implicit contextSupport: ContextSupport): Seq[ServicesCard] = {
    val intent = toNineCardIntent(app)
    cards map (_.copy(
      term = app.name,
      cardType = AppCardType.name,
      imagePath = resourceUtils.getPathPackage(app.packageName, app.className),
      intent = nineCardIntentToJson(intent)
    ))
  }

  def toNewPositionCard(card: Card, newPosition: Int) = Card(
    id = card.id,
    position = card.position,
    micros = card.micros,
    term = card.term,
    packageName = card.packageName,
    cardType = card.cardType,
    intent = card.intent,
    imagePath = card.imagePath,
    starRating = card.starRating,
    numDownloads = card.numDownloads,
    notification = card.notification)

  def toUpdatedCard(card: Card, name: String) = Card(
    id = card.id,
    position = card.position,
    micros = card.micros,
    term = name,
    packageName = card.packageName,
    cardType = card.cardType,
    intent = card.intent,
    imagePath = card.imagePath,
    starRating = card.starRating,
    numDownloads = card.numDownloads,
    notification = card.notification)

  def toAddCardRequestByContacts(items: Seq[UnformedContact]): Seq[ServicesAddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestByContact(zipped._1, zipped._2))

  def toAddCardRequestByContact(item: UnformedContact, position: Int) = {
    val (intent: NineCardIntent, cardType: String) = toNineCardIntent(item)
    ServicesAddCardRequest(
      position = position,
      term = item.name,
      packageName = None,
      cardType = cardType,
      intent = nineCardIntentToJson(intent),
      imagePath = item.photoUri)
  }

  def toPrivateCard(unformedApp: UnformedApp) =
    PrivateCard(
      term = unformedApp.name,
      packageName = Some(unformedApp.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(unformedApp),
      imagePath = unformedApp.imagePath)

}
