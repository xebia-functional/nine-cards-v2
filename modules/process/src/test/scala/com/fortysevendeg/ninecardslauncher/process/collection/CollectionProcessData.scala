package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.process.collection.models.{NineCardIntent, Collection, Card}
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.services.persistence.{models => servicesModel}
import play.api.libs.json.Json

import scala.util.Random

trait CollectionProcessData {

  val collectionId = Random.nextInt(10)
  val nonExistentCollectionId = Random.nextInt(10) + 100
  val name: String = Random.nextString(5)
  val collectionType: String = Random.nextString(5)
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: String = Random.nextString(5)
  val constrains: String = Random.nextString(5)
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)
  val nonExistentSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()

  val cardId = Random.nextInt(10)
  val position: Int = Random.nextInt(10)
  val micros: Int = Random.nextInt(10)
  val term: String = Random.nextString(5)
  val packageName = Random.nextString(5)
  val cardType: String = Random.nextString(5)
  val imagePath: String = Random.nextString(5)
  val starRating = Random.nextDouble()
  val numDownloads = Random.nextString(5)
  val notification: String = Random.nextString(5)
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""

  def createSeqCollection(
    num: Int = 5,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: String = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    constrains: String = constrains,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    cards: Seq[Card] = seqCard) = (0 until 5) map (
    item =>
      Collection(
        id = id + item,
        position = position,
        name = name,
        collectionType = collectionType,
        icon = icon,
        themedColorIndex = themedColorIndex,
        appsCategory = Option(appsCategory),
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
    collectionType: String = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    constrains: String = constrains,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed) = (0 until 5) map (
    item =>
      servicesModel.Collection(
        id = id + item,
        position = position,
        name = name,
        collectionType = collectionType,
        icon = icon,
        themedColorIndex = themedColorIndex,
        appsCategory = Option(appsCategory),
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
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    notification: String = notification) = (0 until 5) map (
    item =>
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
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    notification: String = notification) = (0 until 5) map (
    item =>
      servicesModel.Card(
        id = id + item,
        position = position,
        micros = micros,
        term = term,
        packageName = Option(packageName),
        cardType = cardType,
        intent = intent,
        imagePath = imagePath,
        starRating = Option(starRating),
        numDownloads = Option(numDownloads),
        notification = Option(notification)))

  val seqCard = createSeqCard()
  val card = seqCard.head
  val seqServicesCard = createSeqServicesCard()
  val servicesCard = seqServicesCard.head

  val seqCollection = createSeqCollection()
  val collection = seqCollection.head
  val seqServicesCollection = createSeqServicesCollection()
  val servicesCollection = seqServicesCollection.head
}
