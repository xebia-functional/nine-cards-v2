package com.fortysevendeg.repository.card

import com.fortysevendeg.ninecardslauncher.repository.model.{CardData, Card}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CardEntityData, CardEntity}

import scala.util.Random

trait CardRepositoryTestData {

  val cardId = Random.nextInt(10)
  val nonExistingCardId = 15
  val position = Random.nextInt(10)
  val collectionId = Random.nextInt(10)
  val nonExistingCollectionId = 15
  val term = Random.nextString(5)
  val packageName = Random.nextString(5)
  val `type` = Random.nextString(5)
  val intent = Random.nextString(5)
  val imagePath = Random.nextString(5)
  val starRating = Random.nextDouble()
  val micros = Random.nextInt(5)
  val numDownloads = Random.nextString(10)
  val notification = Random.nextString(10)
  val packageNameOption = Option(packageName)
  val starRatingOption = Option(starRating)
  val numDownloadsOption = Option(numDownloads)
  val notificationOption = Option(notification)

  val cardEntitySeq = createCardEntitySeq(5)
  val cardEntity = cardEntitySeq.head
  val cardSeq = createCardSeq(5)
  val card = cardSeq.head

  def createCardEntitySeq(num: Int) = (0 until num) map (i => CardEntity(
    id = cardId + i,
    data = CardEntityData(
      position = position,
      collectionId = collectionId,
      term = term,
      packageName = packageName,
      `type` = `type`,
      intent = intent,
      imagePath = imagePath,
      starRating = starRating,
      micros = micros,
      numDownloads = numDownloads,
      notification = notification)))

  def createCardSeq(num: Int) = (0 until num) map (i => Card(
    id = cardId + i,
    data = CardData(
      position = position,
      term = term,
      packageName = packageNameOption,
      cardType = `type`,
      intent = intent,
      imagePath = imagePath,
      starRating = starRatingOption,
      micros = micros,
      numDownloads = numDownloadsOption,
      notification = notificationOption)))

  def createInsertCardValues = Map[String, Any](
    CardEntity.position -> position,
    CardEntity.collectionId -> collectionId,
    CardEntity.term -> term,
    CardEntity.packageName -> (packageNameOption getOrElse ""),
    CardEntity.cardType -> `type`,
    CardEntity.intent -> intent,
    CardEntity.imagePath -> imagePath,
    CardEntity.starRating -> (starRatingOption getOrElse 0.0d),
    CardEntity.micros -> micros,
    CardEntity.numDownloads -> (numDownloadsOption getOrElse ""),
    CardEntity.notification -> (notificationOption getOrElse ""))

  def createUpdateCardValues = Map[String, Any](
    CardEntity.position -> position,
    CardEntity.term -> term,
    CardEntity.packageName -> (packageNameOption getOrElse ""),
    CardEntity.cardType -> `type`,
    CardEntity.intent -> intent,
    CardEntity.imagePath -> imagePath,
    CardEntity.starRating -> (starRatingOption getOrElse 0.0d),
    CardEntity.micros -> micros,
    CardEntity.numDownloads -> (numDownloadsOption getOrElse ""),
    CardEntity.notification -> (notificationOption getOrElse ""))

  def createCardData = CardData(
    position = position,
    term = term,
    packageName = packageNameOption,
    cardType = `type`,
    intent = intent,
    imagePath = imagePath,
    starRating = starRatingOption,
    micros = micros,
    numDownloads = numDownloadsOption,
    notification = notificationOption)
}
