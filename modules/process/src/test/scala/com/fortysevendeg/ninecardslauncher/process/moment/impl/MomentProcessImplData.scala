package com.fortysevendeg.ninecardslauncher.process.moment.impl

import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.commons.models._
import com.fortysevendeg.ninecardslauncher.process.commons.types.CardType._
import com.fortysevendeg.ninecardslauncher.process.commons.types.CollectionType._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.moment.models.App
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{App => ServicesApp, Card => ServicesCard, Collection => ServicesCollection, Moment => ServicesMoment, MomentTimeSlot => ServicesMomentTimeSlot}
import play.api.libs.json.Json

import scala.util.Random

trait MomentProcessImplData {

  val collectionId = Random.nextInt(10)
  val name: String = Random.nextString(5)
  val collectionType: CollectionType = collectionTypes(Random.nextInt(collectionTypes.length))
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: NineCardCategory = appsCategories(Random.nextInt(appsCategories.length))
  val constrains: String = Random.nextString(5)
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()

  val name1 = "Scala Android"
  val packageName1 = "com.fortysevendeg.scala.android"
  val className1 = "ScalaAndroidActivity"
  val category1 = "category1"
  val imagePath1 = "imagePath1"
  val colorPrimary1 = "colorPrimary"
  val dateInstalled1 = 1L
  val dateUpdate1 = 1L
  val version1 = "22"
  val installedFromGooglePlay1 = true

  val appId = Random.nextInt(10)
  val momentId = Random.nextInt(10)
  val cardId = Random.nextInt(10)
  val position: Int = Random.nextInt(10)
  val micros: Int = Random.nextInt(10)
  val term: String = Random.nextString(5)
  val packageName = Random.nextString(5)
  val cardType: CardType = cardTypes(Random.nextInt(cardTypes.length))
  val imagePath: String = Random.nextString(5)
  val starRating = Random.nextDouble()
  val numDownloads = Random.nextString(5)
  val notification: String = Random.nextString(5)
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""

  val from = "8:00"
  val to = "19:00"
  val days = Seq(0, 1, 1, 1, 1, 1, 0)

  val collectionId1 = 1
  val homeAppPackageName = "com.google.android.apps.plus"
  val nightAppPackageName = "com.Slack"
  val workAppPackageName = "com.google.android.apps.photos"

  def createSeqCollection(
    num: Int = 3,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: NineCardCategory = appsCategory,
    constrains: String = constrains,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    cards: Seq[Card] = seqCard) =
    (0 until num) map (
      item =>
        Collection(
          id = id + item,
          position = position,
          name = name,
          collectionType = collectionType,
          icon = icon,
          themedColorIndex = themedColorIndex,
          appsCategory = None,
          constrains = Option(constrains),
          originalSharedCollectionId = Option(originalSharedCollectionId),
          sharedCollectionId = Option(sharedCollectionId),
          sharedCollectionSubscribed = sharedCollectionSubscribed,
          cards = cards))

  def createSeqMomentCollection(
     num: Int = 3,
     id: Int = collectionId,
     position: Int = position,
     name: String = name,
     collectionType: Seq[CollectionType] = momentsCollectionTypes,
     icon: String = icon,
     themedColorIndex: Int = themedColorIndex,
     appsCategory: NineCardCategory = appsCategory,
     constrains: String = constrains,
     originalSharedCollectionId: String = originalSharedCollectionId,
     sharedCollectionId: String = sharedCollectionId,
     sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
     cards: Seq[Card] = seqCard) =
    (0 until num) map (
      item =>
        Collection(
          id = id + item,
          position = position,
          name = name,
          collectionType = collectionType(item),
          icon = icon,
          themedColorIndex = themedColorIndex,
          appsCategory = None,
          constrains = Option(constrains),
          originalSharedCollectionId = Option(originalSharedCollectionId),
          sharedCollectionId = Option(sharedCollectionId),
          sharedCollectionSubscribed = sharedCollectionSubscribed,
          cards = cards))

  def createSeqServicesCollection(
    num: Int = 5,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: NineCardCategory = appsCategory,
    constrains: String = constrains,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed) =
    (0 until num) map (item =>
      ServicesCollection(
        id = id + item,
        position = position,
        name = name,
        collectionType = collectionType.name,
        icon = icon,
        themedColorIndex = themedColorIndex,
        appsCategory = Option(appsCategory.name),
        constrains = Option(constrains),
        originalSharedCollectionId = Option(originalSharedCollectionId),
        sharedCollectionId = Option(sharedCollectionId),
        sharedCollectionSubscribed = sharedCollectionSubscribed))

  def createSeqCard(
    num: Int = 5,
    id: Int = cardId,
    position: Int = position,
    micros: Int = micros,
    term: String = term,
    packageName: String = packageName,
    cardType: CardType = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    notification: String = notification) =
    (0 until num) map (item =>
      Card(
        id = id + item,
        position = position,
        micros = micros,
        term = term,
        packageName = Option(packageName),
        cardType = cardType,
        intent = Json.parse(intent).as[NineCardIntent],
        imagePath = imagePath,
        starRating = Option(starRating),
        numDownloads = Option(numDownloads),
        notification = Option(notification)))

  def createSeqServicesCard(
    num: Int = 5,
    id: Int = cardId,
    position: Int = position,
    micros: Int = micros,
    term: String = term,
    packageName: String = packageName,
    cardType: CardType = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    notification: String = notification) =
    (1 until num) map (item =>
      ServicesCard(
        id = id + item,
        position = position,
        micros = micros,
        term = term,
        packageName = Option(packageName),
        cardType = cardType.name,
        intent = intent,
        imagePath = imagePath,
        starRating = Option(starRating),
        numDownloads = Option(numDownloads),
        notification = Option(notification)))

  def createSeqServicesApp(
    num: Int = 5,
    id: Int = appId,
    name: String = name1,
    packageName: String = packageName1,
    className: String = className1,
    category: String = category1,
    imagePath: String = imagePath1,
    colorPrimary: String = colorPrimary1,
    dateInstalled: Long = dateInstalled1,
    dateUpdate: Long = dateUpdate1,
    version: String = version1,
    installedFromGooglePlay: Boolean = installedFromGooglePlay1) =
    (1 until num) map (item =>
      ServicesApp(
        id = id + item,
        name = name,
        packageName = packageName,
        className = className,
        category = category,
        imagePath = imagePath,
        colorPrimary = colorPrimary,
        dateInstalled = dateInstalled,
        dateUpdate = dateUpdate,
        version = version,
        installedFromGooglePlay = installedFromGooglePlay))

  def createSeqServicesMoment(
    num: Int = 3,
    id: Int = momentId,
    collectionId: Option[Int] = Option(collectionId1),
    timeslot: Seq[ServicesMomentTimeSlot] = createSeqServicesMomentTimeSlot(),
    wifi: Seq[String] = Seq.empty,
    headphone: Boolean = false) =
    (1 until num) map (item =>
      ServicesMoment(
        id = id + item,
        collectionId = collectionId,
        timeslot = timeslot,
        wifi = wifi,
        headphone = headphone))

  def createSeqServicesMomentTimeSlot(
    from: String = from,
    to: String = to,
    days: Seq[Int] = days)=
    (1 until 3) map (item =>
      ServicesMomentTimeSlot(
        from = from,
        to = to,
        days = days))

  val homeApp =
    App(
      name = name,
      packageName = homeAppPackageName,
      className = className1,
      imagePath = imagePath)

  val workApp =
    App(
      name = name,
      packageName = workAppPackageName,
      className = className1,
      imagePath = imagePath)

  val nightApp =
    App(
      name = name,
      packageName = nightAppPackageName,
      className = className1,
      imagePath = imagePath)

  val seqCard = createSeqCard()

  val seqCollection = createSeqCollection()
  val collection = seqCollection.headOption
  val seqServicesCollection = createSeqServicesCollection()
  val servicesCollection = seqServicesCollection(0)

  val seqServicesApps = createSeqServicesApp()
  val seqApps = Seq(homeApp, workApp, nightApp)
  val seqMomentCollections = createSeqMomentCollection()
  val seqServicesMoments = createSeqServicesMoment()
  val servicesMoment = seqServicesMoments(0)

}
