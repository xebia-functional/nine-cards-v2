package cards.nine.process.collection.impl

import cards.nine.models._
import cards.nine.models.types.CardType._
import cards.nine.models.types._
import cards.nine.services.api.{CategorizedDetailPackage, RankAppsResponse, RankAppsResponseList}

import scala.util.Random

trait CollectionProcessImplData {
//
//   val seqServicesApp = seqServicesCards map { card =>
//    Application(
//      id = card.id,
//      name = card.term,
//      packageName = card.packageName.getOrElse(""),
//      className = "",
//      category = appsCategoryGame,
//      dateInstalled = 0,
//      dateUpdate = 0,
//      version = "",
//      installedFromGooglePlay = false)
//  }
//
//  val categorizedDetailPackages = seqServicesApp map { app =>
//    CategorizedDetailPackage(
//      packageName = app.packageName,
//      title = app.name,
//      category = Option(app.category.name),
//      icon = "",
//      free = true,
//      downloads = "",
//      stars = 0.0)
//  }
//
//  val latitude: Double = 47
//  val longitude: Double = 36
//  val statusCodeOk = 200
//
//  val awarenessLocation =
//    Location(
//      latitude = latitude,
//      longitude = longitude,
//      countryCode = Some("ES"),
//      countryName = Some("Spain"),
//      addressLines = Seq("street", "city", "postal code")
//    )
//
//  val seqCategoryAndPackages =
//    (seqServicesApp map (app => (app.category, app.packageName))).groupBy(_._1).mapValues(_.map(_._2)).toSeq
//
//  def generateRankAppsResponse() = seqCategoryAndPackages map { item =>
//    RankAppsResponse(
//      category = item._1.name,
//      packages = item._2)
//  }
//
//  val rankAppsResponseList = RankAppsResponseList(
//    statusCode = statusCodeOk,
//    items = generateRankAppsResponse())
//
//  val packagesByCategory =
//    seqCategoryAndPackages map { item =>
//      PackagesByCategory(
//        category = item._1,
//        packages = item._2)
//    }
//
//  val termRequest: String = "termRequest"
//  val packageNameRequest = "package.name.request"
//  val cardTypeRequest: CardType = cardTypes(0)
//  val intentRequest = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
//  val imagePathRequest: String = "imagePathRequest"
//
//  val seqAddCardsRequest = Seq(addCardRequest(0), addCardRequest(1))
//
//  val seqCardIds = Seq(1)
//
//  val cardIdReorder = serviceCard.id
//
//  val samePositionReorder = serviceCard.position
//
//  val newPositionReorder = serviceCard.position + 1
//
//  val newNameEditCard = "newNameEditCard"
//
//  val nameApplication = "Scala Android"
//  val packageNameApplication = "com.fortysevendeg.scala.android"
//  val classNameApplication = "ScalaAndroidActivity"
//  val pathApplication = "/example/path1"
//  val categoryApplication = "category1"
//  val dateInstalledApplication = 1L
//  val dateUpdateApplication = 1L
//  val versionApplication = "22"
//  val installedFromGooglePlayApplication = true
//
//  def applicationData(item: Int) = ApplicationData(
//    name = nameApplication,
//    packageName = packageNameApplication + item,
//    className = classNameApplication,
//    category = appsCategoryGame,
//    dateInstalled = dateInstalledApplication,
//    dateUpdate = dateUpdateApplication,
//    version = versionApplication,
//    installedFromGooglePlay = installedFromGooglePlayApplication)
//
//  val applicationData: ApplicationData = applicationData(0)
//
//  val seqApplicationData = Seq(applicationData(0), applicationData(1))
//
//  val collectionIdMoment = 1
//
//  val momentTimeSlot = MomentTimeSlot(
//    from = "8:00",
//    to = "19:00",
//    days = Seq(0, 1, 1, 1, 1, 1, 0))
//
//  val startX: Int = Random.nextInt(8)
//  val startY: Int = Random.nextInt(8)
//  val spanX: Int = Random.nextInt(8)
//  val spanY: Int = Random.nextInt(8)
//
//  def formedWidgets(item: Int) =
//    WidgetData(
//      packageName = packageName + item,
//      className = className + item,
//      startX = startX + item,
//      startY = startY + item,
//      spanX = spanX + item,
//      spanY = spanY + item,
//      widgetType = AppWidgetType,
//      label = None,
//      imagePath = Option(imagePath),
//      intent = None)
//
//  val seqFormedWidgets = Seq(formedWidgets(0), formedWidgets(1))
//
//  val formedMoment = FormedMoment(
//    collectionId = Option(collectionIdMoment),
//    timeslot = Seq(momentTimeSlot),
//    wifi = Seq.empty,
//    headphone = false,
//    momentType = Option(HomeMorningMoment),
//    widgets = Option(seqFormedWidgets))
//
//  def formedCollection(num: Int) = FormedCollection(
//    name = name,
//    originalSharedCollectionId = originalSharedCollectionIdOption,
//    sharedCollectionId = sharedCollectionIdOption,
//    sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
//    items = Seq.empty,
//    collectionType = collectionType,
//    icon = icon,
//    category = Option(appsCategoryGame),
//    moment = Option(formedMoment))
//
//  val seqFormedCollection = Seq(formedCollection(0), formedCollection(1))
}
