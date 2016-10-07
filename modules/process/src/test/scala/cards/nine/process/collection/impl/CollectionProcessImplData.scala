package cards.nine.process.collection.impl

import cards.nine.models.types.CardType._
import cards.nine.models.types.CollectionType._
import cards.nine.models.types.NineCardsCategory._
import cards.nine.models.types._
import cards.nine.models._
import cards.nine.process.collection.models._
import cards.nine.process.collection.{AddCardRequest, AddCollectionRequest, EditCollectionRequest}
import NineCardIntentImplicits._
import cards.nine.process.commons.models.MomentTimeSlot
import cards.nine.process.commons.models._
import cards.nine.services.api.{CategorizedDetailPackage, RankAppsResponse, RankAppsResponseList}
import cards.nine.services.persistence.models.{Card => ServicesCard, Collection => ServicesCollection}
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
  val appsCategoryGame: NineCardsCategory = appsCategories(0)
  val appsCategoryBooksAndReference: NineCardsCategory = appsCategories(1)
  val originalSharedCollectionId: String = "originalSharedCollection"
  val originalSharedCollectionIdOption: Option[String] = Option(originalSharedCollectionId)
  val sharedCollectionId: String = "shareCollectionId"
  val sharedCollectionIdOption: Option[String] = Option(sharedCollectionId)
  val sharedCollectionSubscribedFalse: Boolean = false
  val className: String = "className"

  val sharedCollectionSubscribed: Boolean =
    if (sharedCollectionId == originalSharedCollectionId) Random.nextBoolean()
    else false

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

  val serviceCard = servicesCard(0)

  val seqServicesCards = Seq(servicesCard(0), servicesCard(1))

  def servicesCollection(num: Int = 0) = ServicesCollection(
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

  val servicesCollection: ServicesCollection = servicesCollection(0)

  val seqServicesCollection = Seq(servicesCollection(0), servicesCollection(1))

  val collectionType: CollectionType = collectionTypes(0)

  def determinePublicCollectionStatus(): PublicCollectionStatus =
    if (sharedCollectionIdOption.isDefined && sharedCollectionSubscribedFalse) Subscribed
    else if (sharedCollectionIdOption.isDefined && originalSharedCollectionIdOption == sharedCollectionIdOption) PublishedByOther
    else if (sharedCollectionIdOption.isDefined) PublishedByMe
    else NotPublished

  val publicCollectionStatus = determinePublicCollectionStatus()

  val collection = Collection(
    id = collectionId,
    position = position,
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

  val seqAddCardRequest = seqServicesCards map { c =>
    AddCardRequest(
      term = c.term,
      packageName = c.packageName,
      cardType = CardType(c.cardType),
      intent = Json.parse(c.intent).as[NineCardsIntent],
      imagePath = c.imagePath)
  }

  val addCollectionRequest = AddCollectionRequest(
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryGame),
    cards = seqAddCardRequest,
    moment = None)

  def card(num: Int = 0) = Card(
    id = cardId + num,
    position = position + num,
    term = term,
    packageName = Option(packageName + num),
    cardType = cardType,
    intent = Json.parse(intent).as[NineCardsIntent],
    imagePath = Option(imagePath),
    notification = Option(notification))

  val card: Card = card(0)

  val collectionRemoved: Int = 1
  val collectionsRemoved = 1
  val cardRemoved = 1
  val cardsRemoved = 1
  val updatedCards: Int = 1
  val updatedCard: Int = 1
  val updatedCollection: Int = 1
  val updatedCollections: Int = 1
  val nameCollectionRequest: String = "nameCollectionRequest"
  val iconCollectionRequest: String = "iconCollectionRequest"
  val themedColorIndexRequest: Int = 1

  val editCollectionRequest = EditCollectionRequest(
    name = nameCollectionRequest,
    icon = iconCollectionRequest,
    themedColorIndex = themedColorIndexRequest,
    appsCategory = Option(appsCategoryBooksAndReference))

  val newSharedCollectionId: String = "newSharedCollectionId"

  val seqServicesApp = seqServicesCards map { card =>
    Application(
      id = card.id,
      name = card.term,
      packageName = card.packageName.getOrElse(""),
      className = "",
      category = appsCategoryGame,
      dateInstalled = 0,
      dateUpdate = 0,
      version = "",
      installedFromGooglePlay = false)
  }

  val categorizedDetailPackages = seqServicesApp map { app =>
    CategorizedDetailPackage(
      packageName = app.packageName,
      title = app.name,
      category = Option(app.category.name),
      icon = "",
      free = true,
      downloads = "",
      stars = 0.0)
  }

  val latitude: Double = 47
  val longitude: Double = 36
  val statusCodeOk = 200

  val awarenessLocation =
    Location(
      latitude = latitude,
      longitude = longitude,
      countryCode = Some("ES"),
      countryName = Some("Spain"),
      addressLines = Seq("street", "city", "postal code")
    )

  val seqCategoryAndPackages =
    (seqServicesApp map (app => (app.category, app.packageName))).groupBy(_._1).mapValues(_.map(_._2)).toSeq

  def generateRankAppsResponse() = seqCategoryAndPackages map { item =>
    RankAppsResponse(
      category = item._1.name,
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

  val termRequest: String = "termRequest"
  val packageNameRequest = "package.name.request"
  val cardTypeRequest: CardType = cardTypes(0)
  val intentRequest = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val imagePathRequest: String = "imagePathRequest"

  def addCardRequest(num: Int = 0) = AddCardRequest(
    term = termRequest,
    packageName = Option(packageNameRequest + num),
    cardType = cardTypeRequest,
    intent = Json.parse(intentRequest).as[NineCardsIntent],
    imagePath = Option(imagePathRequest))

  val seqAddCardsRequest = Seq(addCardRequest(0), addCardRequest(1))

  val seqCardIds = Seq(1)

  val cardIdReorder = serviceCard.id

  val samePositionReorder = serviceCard.position

  val newPositionReorder = serviceCard.position + 1

  val newNameEditCard = "newNameEditCard"

  val nameApplication = "Scala Android"
  val packageNameApplication = "com.fortysevendeg.scala.android"
  val classNameApplication = "ScalaAndroidActivity"
  val pathApplication = "/example/path1"
  val categoryApplication = "category1"
  val dateInstalledApplication = 1L
  val dateUpdateApplication = 1L
  val versionApplication = "22"
  val installedFromGooglePlayApplication = true

  def applicationData(item: Int) = ApplicationData(
    name = nameApplication,
    packageName = packageNameApplication + item,
    className = classNameApplication,
    category = appsCategoryGame,
    dateInstalled = dateInstalledApplication,
    dateUpdate = dateUpdateApplication,
    version = versionApplication,
    installedFromGooglePlay = installedFromGooglePlayApplication)

  val applicationData: ApplicationData = applicationData(0)

  val seqApplicationData = Seq(applicationData(0), applicationData(1))

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
