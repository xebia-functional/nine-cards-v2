package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.process.collection.{EditCollectionRequest, AddCardRequest, AddCollectionRequest}
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.services.persistence.{models => servicesModel, AddCardRequest => ServicesAddCardRequest}
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ServiceContact, PhoneHome, ContactPhone, ContactInfo}
import play.api.libs.json.Json

import scala.util.Random

trait CollectionProcessImplData {

  val categories = Seq(game, booksAndReference, business, comics, communication, education,
    entertainment, finance, healthAndFitness, librariesAndDemo, lifestyle, appWallpaper,
    mediaAndVideo, medical, musicAndAudio, newsAndMagazines, personalization, photography,
    productivity, shopping, social, sports, tools, transportation, travelAndLocal, weather, appWidgets)

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
  val newPosition: Int = Random.nextInt(10)
  val oldPosition: Int = Random.nextInt(10)
  val micros: Int = Random.nextInt(10)
  val term: String = Random.nextString(5)
  val packageName = Random.nextString(5)
  val className = Random.nextString(5)
  val cardType: String = Random.nextString(5)
  val imagePath: String = Random.nextString(5)
  val starRating = Random.nextDouble()
  val numDownloads = Random.nextString(5)
  val ratingsCount = Random.nextInt()
  val commentCount = Random.nextInt()
  val notification: String = Random.nextString(5)
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""

  val lookupKey: String = Random.nextString(5)
  val photoUri: String = Random.nextString(10)
  val phoneNumber: String = Random.nextString(5)

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
    cards: Seq[Card] = seqCard) =
    (0 until 5) map (
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
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed) =
    (0 until 5) map (item =>
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
    notification: String = notification) =
    (0 until 5) map (item =>
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
    notification: String = notification) =
    (0 until 5) map (item =>
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

  def createSeqUnformedItem(num: Int = 150) =
    (0 until num) map { item =>
      UnformedApp(
        name = name,
        packageName = packageName,
        className = className,
        imagePath = imagePath,
        category = categories(Random.nextInt(categories.length)),
        starRating = starRating,
        numDownloads = numDownloads,
        ratingsCount = ratingsCount,
        commentCount = commentCount)
    }

  val seqCard = createSeqCard()
  val card = seqCard.head
  val seqServicesCard = createSeqServicesCard()
  val servicesCard = seqServicesCard.head

  val seqCollection = createSeqCollection()
  val collection = seqCollection.head
  val seqServicesCollection = createSeqServicesCollection()
  val servicesCollection = seqServicesCollection.head

  val unformedItems = createSeqUnformedItem()

  val categoriesUnformedItems: Seq[String] = categories flatMap { category =>
    val count = unformedItems.count(_.category == category)
    if (count >= minAppsToAdd) Option(category) else None
  }

  val collectionForUnformedItem = servicesModel.Collection(
    id = position,
    position = position,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    constrains = Option(constrains),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  def createSeqFormedCollection(num: Int = 150) =
    (0 until num) map { item =>
      FormedCollection(
        name = name,
        originalSharedCollectionId = Option(originalSharedCollectionId),
        sharedCollectionId = Option(sharedCollectionId),
        sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
        items = Seq.empty,
        collectionType = collectionType,
        constrains = Seq(constrains),
        icon = icon,
        category = Option(categories(Random.nextInt(categories.length))))
    }

  val seqFormedCollection = createSeqFormedCollection()

  def createSeqServiceContact(num: Int = 10) =
    (0 until num) map { item =>
      ServiceContact(
        name = name,
        lookupKey = lookupKey,
        photoUri = photoUri,
        favorite = true)
    }

  val seqContacts: Seq[ServiceContact] = createSeqServiceContact()

  val seqContactsWithPhones: Seq[ServiceContact] = seqContacts map {
    _.copy(info = Option(ContactInfo(Seq.empty, Seq(ContactPhone(phoneNumber, PhoneHome)))))
  }

  val addCollectionRequest = AddCollectionRequest(
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory))

  val servicesCollectionAdded = servicesModel.Collection(
    id = seqServicesCollection.size,
    position = seqServicesCollection.size,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    constrains = Option(constrains),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  val collectionAdded = Collection(
    id = seqServicesCollection.size,
    position = seqServicesCollection.size,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    constrains = Option(constrains),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  val editCollectionRequest = EditCollectionRequest(
    name = name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory))

  val updatedCollection = Collection(
    id = seqServicesCollection.head.id,
    position = seqServicesCollection.head.position,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    constrains = Option(constrains),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  val seqAddCardRequest = createSeqAddCardRequest()
  val addCardRequest = seqAddCardRequest.head
  val seqAddCardResponse = createSeqCardResponse()

  def createSeqAddCardRequest(num: Int = 3) =
    (0 until num) map { item =>
      AddCardRequest(
        term = term,
        packageName = Option(packageName),
        intent = Json.parse(intent).as[NineCardIntent],
        imagePath = imagePath)
    }

  def createSeqCardResponse(
    num: Int = 3,
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
    notification: String = notification) =
    (0 until 3) map (item =>
      Card(
        id = id,
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

  def updatedCard = Card(
    id = cardId,
    position = position,
    micros = micros,
    term = name,
    packageName = Option(packageName),
    cardType = cardType,
    intent = Json.parse(intent).as[NineCardIntent],
    imagePath = imagePath,
    starRating = Option(starRating),
    numDownloads = Option(numDownloads),
    notification = Option(notification))
}
