package cards.nine.process.collection

import cards.nine.commons.contexts.ContextSupport
import cards.nine.models.ApplicationData
import cards.nine.models.types.{AppCardType, CardType}
import cards.nine.process.collection.models._
import cards.nine.process.commons.CommonConversions
import cards.nine.process.commons.models.{Card, Collection, NineCardIntent, PrivateCard}
import cards.nine.services.api.models.{PackagesByCategory => ServicesPackagesByCategory}
import cards.nine.services.api.{CategorizedDetailPackage, RankAppsResponse}
import cards.nine.services.persistence.models.{App => ServicesApp, Card => ServicesCard, Collection => ServicesCollection}
import cards.nine.services.persistence.{AddCardRequest => ServicesAddCardRequest, AddCollectionRequest => ServicesAddCollectionRequest, UpdateCardRequest => ServicesUpdateCardRequest, UpdateCardsRequest => ServicesUpdateCardsRequest, UpdateCollectionRequest => ServicesUpdateCollectionRequest, UpdateCollectionsRequest => ServicesUpdateCollectionsRequest, _}

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

  def toAddCardRequestSeq(items: Seq[ApplicationData]): Seq[ServicesAddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestFromUnformedItems(zipped._1, zipped._2))

  def toAddCardRequestFromUnformedItems(item: ApplicationData, position: Int): ServicesAddCardRequest = ServicesAddCardRequest(
    position = position,
    term = item.name,
    packageName = Option(item.packageName),
    cardType = AppCardType.name,
    intent = nineCardIntentToJson(toNineCardIntent(item)),
    imagePath = None)

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
    imagePath = None)

  def toAddCardRequest(collectionId: Int, categorizedPackage: CategorizedDetailPackage, cardType: CardType, position: Int): ServicesAddCardRequest = ServicesAddCardRequest (
    collectionId = Option(collectionId),
    position = position,
    term = categorizedPackage.title,
    packageName = Option(categorizedPackage.packageName),
    cardType = cardType.name,
    intent = nineCardIntentToJson(packageToNineCardIntent(categorizedPackage.packageName)),
    imagePath = None)

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

  def toInstalledApp(cards: Seq[ServicesCard], app: ApplicationData)(implicit contextSupport: ContextSupport): Seq[ServicesCard] = {
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
      imagePath = Option(item.photoUri))
  }

  def toPrivateCard(unformedApp: ApplicationData): PrivateCard =
    PrivateCard(
      term = unformedApp.name,
      packageName = Some(unformedApp.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(unformedApp),
      imagePath = None)

  def toServicesPackagesByCategory(packagesByCategory: (String, Seq[String])) = {
    val (category, packages) = packagesByCategory
    ServicesPackagesByCategory(
      category = category,
      packages = packages)
  }

  def toPackagesByCategory(item: RankAppsResponse) =
    PackagesByCategory(
      category = item.category,
      packages = item.packages)

}
