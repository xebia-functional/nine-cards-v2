package cards.nine.process.collection.impl

import cards.nine.process.collection.models._
import cards.nine.process.collection.{AddCardRequest, AddCollectionRequest, CollectionProcessConfig, EditCollectionRequest}
import cards.nine.process.commons.models.NineCardIntentImplicits._
import cards.nine.process.commons.models._
import cards.nine.models.Spaces
import cards.nine.models.Spaces._
import cards.nine.models.types.CardType._
import cards.nine.models.types.CollectionType._
import cards.nine.models.types.NineCardCategory._
import cards.nine.models.types._
import cards.nine.services.api.{CategorizedDetailPackage, RankAppsResponse, RankAppsResponseList}
import cards.nine.services.apps.models.Application
import cards.nine.services.awareness.AwarenessLocation
import cards.nine.services.contacts.models.{Contact => ServicesContact, ContactInfo => ServicesContactInfo, ContactPhone => ServicesContactPhone}
import cards.nine.services.persistence.models.{App => ServicesApp, Card => ServicesCard, Collection => ServicesCollection}
import cards.nine.services.persistence.{UpdateCardRequest => ServicesUpdateCardRequest, UpdateCardsRequest => ServicesUpdateCardsRequest}
import play.api.libs.json.Json

import scala.util.Random

trait CollectionProcessImplData {

  val collectionId = 1
  val position: Int = 1
  val newPosition: Int = 2
  val name: String = "name"
  val collectionTypeAppsType: CollectionType = collectionTypes(0)
  val icon: String = "icon"
  val themedColorIndex: Int = 1
  val appsCategoryGame: NineCardCategory = appsCategories(0)
  val appsCategoryBooksAndReference: NineCardCategory = appsCategories(1)
  val originalSharedCollectionId: String = "originalSharedCollection"
  val originalSharedCollectionIdOption: Option[String] = Option(originalSharedCollectionId)
  val sharedCollectionId: String = "shareCollectionId"
  val sharedCollectionIdOption: Option[String] = Option(sharedCollectionId)
  val sharedCollectionSubscribedFalse: Boolean = false
  val className: String = "className"

  val servicesCollection = ServicesCollection(
    id = collectionId,
    position = position,
    name = name,
    collectionType = collectionTypeAppsType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame.name),
    cards = Seq.empty,
    moment = None,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse)

  val cardId: Int = 1
  val term: String = "term"
  val packageName = "package.name."
  val cardType: CardType = cardTypes(0)
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val imagePath: String = "imagePath"
  val notification: String = "notification"

  def servicesCard(num: Int = 0) = ServicesCard(
    id = cardId + num,
    position = position + num,
    term = term,
    packageName = Option(packageName + num),
    cardType = cardType.name,
    intent = intent,
    imagePath = Option(imagePath),
    notification = Option(notification))

  val seqServicesCards = Seq(servicesCard(0), servicesCard(1))

  def servicesCollectionWithCards(num: Int = 0) = ServicesCollection(
    id = collectionId + num,
    position = position + num,
    name = name,
    collectionType = collectionTypeAppsType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame.name),
    cards = seqServicesCards,
    moment = None,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse)

  val seqServicesCollectionWithCards = Seq(servicesCollectionWithCards(0), servicesCollectionWithCards(1))

  def servicesCollectionWithoutCards(num: Int = 0) = ServicesCollection(
    id = collectionId + num,
    position = position + num,
    name = name,
    collectionType = collectionTypeAppsType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame.name),
    cards = Seq.empty,
    moment = None,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse)

  val seqServicesCollectionWithoutCards = Seq(servicesCollectionWithoutCards(0), servicesCollectionWithoutCards(1))


  val servicesCollectionForUnformedItem = ServicesCollection(
    id = position,
    position = position,
    name = name,
    collectionType = collectionTypeAppsType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame.name),
    cards = Seq.empty,
    moment = None,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse)

  val unformedApp = UnformedApp(
    name = name,
    packageName = packageName,
    className = className,
    category = appsCategoryGame)

  val seqUnformedApps = Seq(unformedApp, unformedApp)

  val lookupKey: String = "lookupKey"
  val photoUri: String = "photoUri"
  val phoneNumber: String = "phoneNumber"

  val unformedContact = UnformedContact(
    name = name,
    lookupKey = lookupKey,
    photoUri = photoUri,
    info = Option(ContactInfo(Seq.empty, Seq(ContactPhone(phoneNumber, PhoneHome.toString)))))

  val seqUnformedContact = Seq(unformedContact, unformedContact, unformedContact, unformedContact, unformedContact)

  val categoriesUnformedApps: Seq[NineCardCategory] = allCategories flatMap { category =>
    val count = seqUnformedApps.count(_.category == category)
    if (count >= minAppsToAdd) Option(category) else None
  }

  val categoriesUnformedItems: Seq[NineCardCategory] = {
    val count = seqUnformedContact.size
    if (count >= minAppsToAdd) categoriesUnformedApps :+ ContactsCategory else categoriesUnformedApps
  }


  val collectionType: CollectionType = collectionTypes(0)

  def determinePublicCollectionStatus(): PublicCollectionStatus =
    if (sharedCollectionIdOption.isDefined && sharedCollectionSubscribedFalse) Subscribed
    else if (sharedCollectionIdOption.isDefined && originalSharedCollectionIdOption == sharedCollectionIdOption) PublishedByOther
    else if (sharedCollectionIdOption.isDefined) PublishedByMe
    else NotPublished

  val publicCollectionStatus = determinePublicCollectionStatus()

  val seqServicesCollectionAddWithoutCards = Seq(servicesCollectionWithoutCards(0), servicesCollectionWithoutCards(1))

  val seqServicesCollectionAddWithCards = Seq(servicesCollectionWithCards(0), servicesCollectionWithCards(1))

  val addCollectionRequest = AddCollectionRequest(
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame),
    cards = Seq.empty,
    moment = None)

  val seqAddCardRequest = seqServicesCards map { c =>
    AddCardRequest(
      term = c.term,
      packageName = c.packageName,
      cardType = CardType(c.cardType),
      intent = Json.parse(c.intent).as[NineCardIntent],
      imagePath = c.imagePath)
  }

  val addCollectionRequestWithCards = AddCollectionRequest(
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame),
    cards = seqAddCardRequest,
    moment = None)

  val servicesCollectionAddedWithoutCards = ServicesCollection(
    id = seqServicesCollectionAddWithoutCards.size,
    position = seqServicesCollectionAddWithoutCards.size,
    name = name,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame.name),
    cards = Seq.empty,
    moment = None,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse)

  val servicesCollectionAddedWithCards = ServicesCollection(
    id = seqServicesCollectionAddWithoutCards.size,
    position = seqServicesCollectionAddWithoutCards.size,
    name = name,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame.name),
    cards = seqServicesCards,
    moment = None,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse)

  val collectionAddedWithoutCards = Collection(
    id = seqServicesCollectionAddWithoutCards.size,
    position = seqServicesCollectionAddWithoutCards.size,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame),
    cards = Seq.empty,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse,
    publicCollectionStatus = publicCollectionStatus)

  def card(num: Int = 0) = Card(
    id = cardId + num,
    position = position + num,
    term = term,
    packageName = Option(packageName + num),
    cardType = cardType,
    intent = Json.parse(intent).as[NineCardIntent],
    imagePath = Option(imagePath),
    notification = Option(notification))

  val collectionAddedWithCards = Collection(
    id = seqServicesCollectionAddWithoutCards.size,
    position = seqServicesCollectionAddWithoutCards.size,
    name = name,
    collectionType = collectionType,
    icon = icon,
    cards = Seq(card(0), card(1)),
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse,
    publicCollectionStatus = publicCollectionStatus)

  val collectionRemoved: Int = 1

  val collectionsRemoved = 1

  val cardRemoved = 1

  val cardsRemoved = 1

  val updatedCards: Int = 1

  val updatedCard: Int = 1

  val updatedCollection: Int = 1

  val updatedCollections: Int = 1

  val servicesCollectionDelete = ServicesCollection(
    id = collectionId,
    position = position,
    name = name,
    collectionType = collectionTypeAppsType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame.name),
    cards = Seq.empty,
    moment = None,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse)

  val servicesCollectionByPosition = ServicesCollection(
    id = collectionId,
    position = position,
    name = name,
    collectionType = collectionTypeAppsType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame.name),
    cards = Seq.empty,
    moment = None,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse)


  val nameCollectionRequest: String = "nameCollectionRequest"
  val iconCollectionRequest: String = "iconCollectionRequest"
  val themedColorIndexRequest: Int = 1

  val editCollectionRequest = EditCollectionRequest(
    name = nameCollectionRequest,
    icon = iconCollectionRequest,
    themedColorIndex = themedColorIndexRequest,
    appsCategory = Option(appsCategoryBooksAndReference))

  val editedCollection = Collection(
    id = collectionId,
    position = position,
    name = nameCollectionRequest,
    collectionType = collectionType,
    icon = iconCollectionRequest,
    themedColorIndex = themedColorIndexRequest,
    appsCategory = Option(appsCategoryBooksAndReference),
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse,
    publicCollectionStatus = publicCollectionStatus)

  val newSharedCollectionId: String = "newSharedCollectionId"

  val updatedSharedCollection = Collection(
    id = collectionId,
    position = position,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame),
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = Option(newSharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribedFalse,
    publicCollectionStatus = publicCollectionStatus)

  val seqServicesCardsAddPackages = Seq(servicesCard(0), servicesCard(1))

  val seqServicesAppAddPackages = seqServicesCardsAddPackages map { card =>
    ServicesApp(
      id = card.id,
      name = card.term,
      packageName = card.packageName.getOrElse(""),
      className = "",
      category = appsCategoryGame.name,
      dateInstalled = 0,
      dateUpdate = 0,
      version = "",
      installedFromGooglePlay = false)
  }

  val categorizedDetailPackages = seqServicesAppAddPackages map { app =>
    CategorizedDetailPackage(
      packageName = app.packageName,
      title = app.name,
      category = Some(app.category),
      icon = "",
      free = true,
      downloads = "",
      stars = 0.0)
  }

  val seqServicesCardsRank = Seq(servicesCard(0), servicesCard(1))

  val seqServicesAppRank = seqServicesCardsAddPackages map { card =>
    ServicesApp(
      id = card.id,
      name = card.term,
      packageName = card.packageName.getOrElse(""),
      className = "",
      category = appsCategoryGame.name,
      dateInstalled = 0,
      dateUpdate = 0,
      version = "",
      installedFromGooglePlay = false)
  }

  val latitude: Double = 47
  val longitude: Double = 36
  val statusCodeOk = 200

  val awarenessLocation =
    AwarenessLocation(
      latitude = latitude,
      longitude = longitude,
      countryCode = Some("ES"),
      countryName = Some("Spain"),
      addressLines = Seq("street", "city", "postal code")
    )

  val seqCategoryAndPackages =
    (seqServicesAppRank map (app => (app.category, app.packageName))).groupBy(_._1).mapValues(_.map(_._2)).toSeq

  def generateRankAppsResponse() = seqCategoryAndPackages map { item =>
    RankAppsResponse(
      category = item._1,
      packages = item._2)
  }

  val rankAppsResponseList = RankAppsResponseList(
    statusCode = statusCodeOk,
    items = generateRankAppsResponse())

  val packagesByCategory =
    seqCategoryAndPackages map { item =>
      PackagesByCategory(
        category = item._1,
        packages = item._2)
    }

  val seqServicesCardByCollection = Seq(servicesCard(0), servicesCard(1))

  val termRequest: String = "termRequest"
  val packageNameRequest = "package.name.request"
  val cardTypeRequest: CardType = cardTypes(0)
  val intentRequest = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val imagePathRequest: String = "imagePathRequest"

  val seqServicesAddedCards = Seq(servicesCard(0), servicesCard(1))

  def addCardRequest(num: Int = 0) = AddCardRequest(
    term = termRequest,
    packageName = Option(packageNameRequest + num),
    cardType = cardTypeRequest,
    intent = Json.parse(intentRequest).as[NineCardIntent],
    imagePath = Option(imagePathRequest))

  val seqAddCardsRequest = Seq(addCardRequest(0), addCardRequest(1))

  val seqServicesCardDelete = Seq(servicesCard(0), servicesCard(1), servicesCard(2))

  val seqCardIds = Seq(1, 2)

  val seqServicesCardReorder = Seq(servicesCard(0), servicesCard(1), servicesCard(2))

  val servicesCardReorder = servicesCard(0)

  val cardIdReorder = servicesCardReorder.id

  val samePositionReorder = servicesCardReorder.position

  val newPositionReorder = servicesCardReorder.position + 1

  val editCard = servicesCard(0)

  val cardIdEdit = editCard.id

  val newNameEditCard = "newNameEditCard"

  val editedCard = Card(
    id = editCard.id,
    position = editCard.position,
    term = newNameEditCard,
    packageName = editCard.packageName,
    cardType = cardType,
    intent = Json.parse(editCard.intent).as[NineCardIntent],
    imagePath = editCard.imagePath,
    notification = editCard.notification)


  val nameApplication = "Scala Android"
  val packageNameApplication = "com.fortysevendeg.scala.android"
  val classNameApplication = "ScalaAndroidActivity"
  val pathApplication = "/example/path1"
  val categoryApplication = "category1"
  val dateInstalledApplication = 1L
  val dateUpdateApplication = 1L
  val versionApplication = "22"
  val installedFromGooglePlayApplication = true

  val applicationUpdateNoInstallation = Application(
    name = nameApplication,
    packageName = packageNameApplication,
    className = classNameApplication,
    dateInstalled = dateInstalledApplication,
    dateUpdate = dateUpdateApplication,
    version = versionApplication,
    installedFromGooglePlay = installedFromGooglePlayApplication)

  val seqServicesCardUpdateNoInstallation = Seq(servicesCard(0), servicesCard(1))

  val collectionForUnformedItem = ServicesCollection(
    id = position,
    position = position,
    name = name,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame.name),
    cards = Seq.empty,
    moment = None,
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  val seqUnformedAppsForPrivateCollections: Seq[UnformedApp] =
    Seq(
      UnformedApp(
        name = "nameUnformed0",
        packageName = "package.name.0",
        className = "classNameUnformed0",
        category = appsCategories(0)),
      UnformedApp(
        name = "nameUnformed1",
        packageName = "package.name.1",
        className = "classNameUnformed1",
        category = appsCategories(1)))

  val sharedCollectionSubscribed: Boolean =
    if (sharedCollectionId == originalSharedCollectionId) Random.nextBoolean()
    else false

  val collectionIdMoment = 1

  val momentTimeSlot = MomentTimeSlot(
    from = "8:00",
    to = "19:00",
    days = Seq(0, 1, 1, 1, 1, 1, 0))

  val startX: Int = Random.nextInt(8)
  val startY: Int = Random.nextInt(8)
  val spanX: Int = Random.nextInt(8)
  val spanY: Int = Random.nextInt(8)

  def formedWidgets(item: Int) =
    FormedWidget(
      packageName = packageName + item,
      className = className + item,
      startX = startX + item,
      startY = startY + item,
      spanX = spanX + item,
      spanY = spanY + item,
      widgetType = AppWidgetType,
      label = None,
      imagePath = Option(imagePath),
      intent = None)

  val seqFormedWidgets = Seq(formedWidgets(0), formedWidgets(1))

  val formedMoment = FormedMoment(
    collectionId = Option(collectionIdMoment),
    timeslot = Seq(momentTimeSlot),
    wifi = Seq.empty,
    headphone = false,
    momentType = Option(HomeMorningMoment),
    widgets = Option(seqFormedWidgets))

  def formedCollection(num: Int) = FormedCollection(
    name = name,
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
    items = Seq.empty,
    collectionType = collectionType,
    icon = icon,
    category = Option(appsCategoryGame),
    moment = Option(formedMoment))

  val seqFormedCollection = Seq(formedCollection(0), formedCollection(1))

}
