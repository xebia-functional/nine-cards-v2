package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.commons.CardType
import com.fortysevendeg.ninecardslauncher.process.commons.CardType._
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCollectionRequest => ServicesAddCollectionRequest,
  UpdateCollectionRequest => ServicesUpdateCollectionRequest, AddCardRequest => ServicesAddCardRequest, UpdateCardRequest => ServicesUpdateCardRequest, _}
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
    sharedCollectionId = servicesCollection.sharedCollectionId,
    sharedCollectionSubscribed = servicesCollection.sharedCollectionSubscribed,
    cards = servicesCollection.cards map toCard)

  def toAddCollectionRequest(addCollectionRequest: AddCollectionRequest, position: Int) = ServicesAddCollectionRequest(
    position = position,
    name = addCollectionRequest.name,
    collectionType = addCollectionRequest.collectionType,
    icon = addCollectionRequest.icon,
    themedColorIndex = addCollectionRequest.themedColorIndex,
    appsCategory = addCollectionRequest.appsCategory,
    constrains = None,
    originalSharedCollectionId = None,
    sharedCollectionId = None,
    sharedCollectionSubscribed = None,
    cards = Seq())

  def toFindCollectionByIdRequest(collectionId: Int) = FindCollectionByIdRequest(
    id = collectionId)

  def toServicesUpdateCollectionRequest(collection: Collection) = ServicesUpdateCollectionRequest(
    id = collection.id,
    position = collection.position,
    name = collection.name,
    collectionType = collection.collectionType,
    icon = collection.icon,
    themedColorIndex = collection.themedColorIndex,
    appsCategory = collection.appsCategory,
    constrains = collection.constrains,
    originalSharedCollectionId = collection.originalSharedCollectionId,
    sharedCollectionId = collection.sharedCollectionId,
    sharedCollectionSubscribed = Option(collection.sharedCollectionSubscribed),
    cards = collection.cards map toServicesCard)

  def toNewPositionCollection(collection: Collection, newPosition: Int) =  Collection(
    id = collection.id,
    position = newPosition,
    name = collection.name,
    collectionType = collection.collectionType,
    icon = collection.icon,
    themedColorIndex = collection.themedColorIndex,
    appsCategory = collection.appsCategory,
    constrains = collection.constrains,
    originalSharedCollectionId = collection.originalSharedCollectionId,
    sharedCollectionId = collection.sharedCollectionId,
    sharedCollectionSubscribed = collection.sharedCollectionSubscribed,
    cards = collection.cards)

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

  def toFetchCacheCategoryByPackageRequest(appsCategory: String) = FetchCacheCategoryByPackageRequest(
    packageName = appsCategory)

  def toCardSeq(servicesCardSeq: Seq[ServicesCard]) = servicesCardSeq map toCard

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

  def toServicesCard(card: Card) = ServicesCard(
    id = card.id,
    position = card.position,
    micros = card.micros,
    term = card.term,
    packageName = card.packageName,
    cardType = card.cardType,
    intent = card.intent.toString,
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
    cardType = app,
    intent = nineCardIntentToJson(toNineCardIntent(item)),
    imagePath = item.imagePath)

  def toFetchCardsByCollectionRequest(collectionRequestId: Int) = FetchCardsByCollectionRequest(
    collectionId = collectionRequestId)

  def toAddCardRequest(collectionId: Int, addCardRequest: AddCardRequest, position: Int) = ServicesAddCardRequest (
    collectionId = Option(collectionId),
    position = position,
    term = addCardRequest.term,
    packageName = addCardRequest.packageName,
    cardType = addCardRequest.cardType,
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
    cardType = card.cardType,
    intent = card.intent.toString,
    imagePath = card.imagePath,
    starRating = card.starRating,
    numDownloads = card.numDownloads,
    notification = card.notification)

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


  def toNineCardIntent(item: UnformedApp) = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(item.packageName),
      class_name = Option(item.className)))
    intent.setAction(openApp)
    intent.setClassName(item.packageName, item.className)
    intent
  }

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

  def toNineCardIntent(item: UnformedContact): (NineCardIntent, String) = item match {
    case UnformedContact(_, _, _, Some(info)) if info.phones.nonEmpty =>
      val phone = info.phones.headOption map (_.number)
      val intent = NineCardIntent(NineCardIntentExtras(tel = phone))
      intent.setAction(openPhone)
      (intent, CardType.phone)
    case UnformedContact(_, _, _, Some(info)) if info.emails.nonEmpty =>
      val address = info.emails.headOption map (_.address)
      val intent = NineCardIntent(NineCardIntentExtras(email = address))
      intent.setAction(openEmail)
      (intent, CardType.email)
    case _ => // TODO 9C-234 - We should create a new action for open contact and use it here
      val intent = NineCardIntent(NineCardIntentExtras())
      (intent, CardType.app)
  }

  private[this] def jsonToNineCardIntent(json: String) = Json.parse(json).as[NineCardIntent]

  private[this] def nineCardIntentToJson(intent: NineCardIntent) = Json.toJson(intent).toString()

}
