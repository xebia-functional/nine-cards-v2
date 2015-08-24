package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.commons.CardType
import com.fortysevendeg.ninecardslauncher.process.commons.CardType._
import com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindCollectionByIdRequest, FetchCacheCategoryByPackageRequest, AddCollectionRequest => ServicesAddCollectionRequest, AddCardRequest}
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
    cards = Seq()
  )

  def toFindCollectionByIdRequest(collectionId: Int) = FindCollectionByIdRequest(
    id = collectionId
  )

  def movedCollectionPosition(collection: Collection, value: Int): Collection =  Collection(
    id = collection.id,
    position = collection.position - value,
    name = collection.name,
    collectionType = collection.collectionType,
    icon = collection.icon,
    themedColorIndex = collection.themedColorIndex,
    appsCategory = collection.appsCategory,
    constrains = collection.constrains,
    originalSharedCollectionId = collection.originalSharedCollectionId,
    sharedCollectionId = collection.originalSharedCollectionId,
    sharedCollectionSubscribed = collection.sharedCollectionSubscribed,
    cards = collection.cards
  )

  def toFetchCacheCategoryByPackageRequest(appsCategory: String) = FetchCacheCategoryByPackageRequest(
    packageName = appsCategory
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

  def toAddCardRequestByContacts(items: Seq[Contact]): Seq[AddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestByContact(zipped._1, zipped._2))

  def toAddCardRequestByContact(item: Contact, position: Int) = {
    val (intent: NineCardIntent, cardType: String) = toNineCardIntent(item)
    AddCardRequest(
      position = position,
      term = item.name,
      packageName = None,
      cardType = cardType,
      intent = nineCardIntentToJson(intent),
      imagePath = item.photoUri
    )
  }

  def toNineCardIntent(item: Contact): (NineCardIntent, String) = item match {
    case Contact(_, _, _, _, _, Some(info)) if info.phones.nonEmpty =>
      val phone = info.phones.headOption map (_.number)
      val intent = NineCardIntent(NineCardIntentExtras(tel = phone))
      intent.setAction(openPhone)
      (intent, CardType.phone)
    case Contact(_, _, _, _, _, Some(info)) if info.emails.nonEmpty =>
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
