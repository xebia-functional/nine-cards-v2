package com.fortysevendeg.repository.card

import com.fortysevendeg.ninecardslauncher.repository.model.{CardData, Card}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CardEntityData, CardEntity}

import scala.util.Random

trait CardRepositoryTestData {

  val testCardId = Random.nextInt(10)
  val testNonExistingCardId = 15
  val testPosition = Random.nextInt(10)
  val testCollectionId = Random.nextInt(10)
  val testNonExistingCollectionId = 15
  val testTerm = Random.nextString(5)
  val testPackageName = Random.nextString(5)
  val testType = Random.nextString(5)
  val testIntent = Random.nextString(5)
  val testImagePath = Random.nextString(5)
  val testStarRating = Random.nextDouble()
  val testMicros = Random.nextInt(5)
  val testNumDownloads = Random.nextString(10)
  val testNotification = Random.nextString(10)
  val testPackageNameOption = Option(testPackageName)
  val testStarRatingOption = Option(testStarRating)
  val testNumDownloadsOption = Option(testNumDownloads)
  val testNotificationOption = Option(testNotification)

  val cardEntitySeq = createCardEntitySeq(5)
  val cardEntity = cardEntitySeq(0)
  val cardSeq = createCardSeq(5)
  val card = cardSeq(0)

  def createCardEntitySeq(num: Int) = List.tabulate(num)(
    i => CardEntity(
      id = testCardId + i,
      data = CardEntityData(
        position = testPosition,
        collectionId = testCollectionId,
        term = testTerm,
        packageName = testPackageName,
        `type` = testType,
        intent = testIntent,
        imagePath = testImagePath,
        starRating = testStarRating,
        micros = testMicros,
        numDownloads = testNumDownloads,
        notification = testNotification)))

  def createCardSeq(num: Int) = List.tabulate(num)(
    i => Card(
      id = testCardId + i,
      data = CardData(
        position = testPosition,
        term = testTerm,
        packageName = testPackageNameOption,
        cardType = testType,
        intent = testIntent,
        imagePath = testImagePath,
        starRating = testStarRatingOption,
        micros = testMicros,
        numDownloads = testNumDownloadsOption,
        notification = testNotificationOption)))

  def createInsertCardValues = Map[String, Any](
    CardEntity.position -> testPosition,
    CardEntity.collectionId -> testCollectionId,
    CardEntity.term -> testTerm,
    CardEntity.packageName -> (testPackageNameOption orNull),
    CardEntity.cardType -> testType,
    CardEntity.intent -> testIntent,
    CardEntity.imagePath -> testImagePath,
    CardEntity.starRating -> (testStarRatingOption getOrElse 0.0d),
    CardEntity.micros -> testMicros,
    CardEntity.numDownloads -> (testNumDownloadsOption orNull),
    CardEntity.notification -> (testNotificationOption orNull))

  def createUpdateCardValues = Map[String, Any](
    CardEntity.position -> testPosition,
    CardEntity.term -> testTerm,
    CardEntity.packageName -> (testPackageNameOption orNull),
    CardEntity.cardType -> testType,
    CardEntity.intent -> testIntent,
    CardEntity.imagePath -> testImagePath,
    CardEntity.starRating -> (testStarRatingOption getOrElse 0.0d),
    CardEntity.micros -> testMicros,
    CardEntity.numDownloads -> (testNumDownloadsOption orNull),
    CardEntity.notification -> (testNotificationOption orNull))

  def createCardData = CardData(
    position = testPosition,
    term = testTerm,
    packageName = testPackageNameOption,
    cardType = testType,
    intent = testIntent,
    imagePath = testImagePath,
    starRating = testStarRatingOption,
    micros = testMicros,
    numDownloads = testNumDownloadsOption,
    notification = testNotificationOption)
}
