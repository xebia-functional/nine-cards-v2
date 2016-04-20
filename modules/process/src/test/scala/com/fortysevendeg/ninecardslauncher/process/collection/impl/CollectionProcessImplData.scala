package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCardRequest, AddCollectionRequest, EditCollectionRequest}
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.commons.models._
import com.fortysevendeg.ninecardslauncher.process.commons.types.CardType._
import com.fortysevendeg.ninecardslauncher.process.commons.types.CollectionType._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{CardType, CollectionType, ContactsCategory, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.commons.PhoneHome
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ServicesContact, ContactInfo => ServicesContactInfo, ContactPhone => ServicesContactPhone}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard, Collection => ServicesCollection, Moment => ServicesMoment, MomentTimeSlot => ServicesMomentTimeSlot}
import play.api.libs.json.Json

import scala.util.Random

trait CollectionProcessImplData {

  val collectionId = Random.nextInt(10)
  val nonExistentCollectionId = Random.nextInt(10) + 100
  val name: String = Random.nextString(5)
  val collectionType: CollectionType = collectionTypes(Random.nextInt(collectionTypes.length))
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: NineCardCategory = appsCategories(Random.nextInt(appsCategories.length))
  val appsCategoryName = appsCategory.name
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)
  val nonExistentSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()

  val name1 = "Scala Android"
  val packageName1 = "com.fortysevendeg.scala.android"
  val className1 = "ScalaAndroidActivity"
  val path1 = "/example/path1"
  val category1 = "category1"
  val imagePath1 = "imagePath1"
  val resourceIcon1 = 1
  val dateInstalled1 = 1L
  val dateUpdate1 = 1L
  val version1 = "22"
  val installedFromGooglePlay1 = true

  val cardId = Random.nextInt(10)
  val position: Int = Random.nextInt(10)
  val newPosition: Int = position + Random.nextInt(10)
  val oldPosition: Int = Random.nextInt(10)
  val term: String = Random.nextString(5)
  val packageName = Random.nextString(5)
  val className = Random.nextString(5)
  val cardType: CardType = cardTypes(Random.nextInt(cardTypes.length))
  val imagePath: String = Random.nextString(5)
  val ratingsCount = Random.nextInt()
  val commentCount = Random.nextInt()
  val notification: String = Random.nextString(5)
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""

  val lookupKey: String = Random.nextString(5)
  val photoUri: String = Random.nextString(10)
  val phoneNumber: String = Random.nextString(5)

  val collectionsRemoved = Random.nextInt(2)
  val cardsRemoved = Random.nextInt(2)

  val momentId = Random.nextInt(5)

  val application1 = Application(
    name = name1,
    packageName = packageName1,
    className = className1,
    resourceIcon = resourceIcon1,
    dateInstalled = dateInstalled1,
    dateUpdate = dateUpdate1,
    version = version1,
    installedFromGooglePlay = installedFromGooglePlay1)

  val collectionId1 = 1

  val collectionId2 = 2

  val collection1 = ServicesCollection(
    id = collectionId1,
    position = position,
    name = name,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory.name),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  val momentTimeSlot = MomentTimeSlot(
    from = "8:00",
    to = "19:00",
    days = Seq(0, 1, 1, 1, 1, 1, 0))

  val moment = Moment(
    collectionId = Option(collectionId1),
    timeslot = Seq(momentTimeSlot),
    wifi = Seq.empty,
    headphone = false)

  def createSeqCollection(
    num: Int = 5,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: NineCardCategory = appsCategory,
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
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed) =
    (0 until 5) map (item =>
      ServicesCollection(
        id = id + item,
        position = position,
        name = name,
        collectionType = collectionType.name,
        icon = icon,
        themedColorIndex = themedColorIndex,
        appsCategory = Option(appsCategory.name),
        originalSharedCollectionId = Option(originalSharedCollectionId),
        sharedCollectionId = Option(sharedCollectionId),
        sharedCollectionSubscribed = sharedCollectionSubscribed))

  def createSeqCard(
    num: Int = 5,
    id: Int = cardId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: CardType = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification) =
    (0 until 5) map (item =>
      Card(
        id = id + item,
        position = position,
        term = term,
        packageName = Option(packageName),
        cardType = cardType,
        intent = Json.parse(intent).as[NineCardIntent],
        imagePath = imagePath,
        notification = Option(notification)))

  def createSeqServicesCard(
    num: Int = 5,
    id: Int = cardId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: CardType = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification) =
    (1 until 5) map (item =>
      ServicesCard(
        id = id + item,
        position = position,
        term = term,
        packageName = Option(packageName),
        cardType = cardType.name,
        intent = intent,
        imagePath = imagePath,
        notification = Option(notification)))

  def createSeqUnformedApps(num: Int = 150) =
    (0 until num) map { item =>
      UnformedApp(
        name = name,
        packageName = packageName,
        className = className,
        imagePath = imagePath,
        category = appsCategory)
    }

  def createSeqUnformedContacs(num: Int = 15) =
    (0 until num) map { item =>
      UnformedContact(
        name = name,
        lookupKey = lookupKey,
        photoUri = photoUri,
        info = Option(ContactInfo(Seq.empty, Seq(ContactPhone(phoneNumber, PhoneHome.toString)))))
    }

  val seqCard = createSeqCard()
  val servicesCard = ServicesCard(
    id = cardId,
    position = position,
    term = term,
    packageName = Option(packageName),
    cardType = cardType.name,
    intent = intent,
    imagePath = imagePath,
    notification = Option(notification))
  val seqServicesCard = Seq(servicesCard) ++ createSeqServicesCard()

  val seqCollection = createSeqCollection()
  val collection = seqCollection.headOption
  val seqServicesCollection = createSeqServicesCollection()
  val servicesCollection = seqServicesCollection.headOption

  val unformedApps = createSeqUnformedApps()
  val unformedContacts = createSeqUnformedContacs()

  val categoriesUnformedApps: Seq[NineCardCategory] = allCategories flatMap { category =>
    val count = unformedApps.count(_.category == category)
    if (count >= minAppsToAdd) Option(category) else None
  }

  val categoriesUnformedItems: Seq[NineCardCategory] = {
    val count = unformedContacts.size
    if (count >= minAppsToAdd) categoriesUnformedApps :+ ContactsCategory else categoriesUnformedApps
  }

  val collectionForUnformedItem = ServicesCollection(
    id = position,
    position = position,
    name = name,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory.name),
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
        icon = icon,
        category = Option(appsCategory),
        moment = Option(moment))
    }

  val seqFormedCollection = createSeqFormedCollection()

  def createSeqServicesContact(num: Int = 10) =
    (0 until num) map { item =>
      ServicesContact(
        name = name,
        lookupKey = lookupKey,
        photoUri = photoUri,
        favorite = true)
    }

  val seqContacts: Seq[ServicesContact] = createSeqServicesContact()

  val seqContactsWithPhones: Seq[ServicesContact] = seqContacts map {
    _.copy(info = Option(ServicesContactInfo(Seq.empty, Seq(ServicesContactPhone(phoneNumber, PhoneHome)))))
  }

  val addCollectionRequest = AddCollectionRequest(
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory))

  val servicesCollectionAdded = ServicesCollection(
    id = seqServicesCollection.size,
    position = seqServicesCollection.size,
    name = name,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryName),
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
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  val editCollectionRequest = EditCollectionRequest(
    name = name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory))

  val updatedCollection = Collection(
    id = collectionId,
    position = position,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  val seqAddCardRequest = createSeqAddCardRequest()
  val addCardRequest = seqAddCardRequest.headOption
  val seqAddCardResponse = createSeqCardResponse()

  def createSeqAddCardRequest(num: Int = 3) =
    (0 until num) map { item =>
      AddCardRequest(
        term = term,
        packageName = Option(packageName),
        cardType = cardType,
        intent = Json.parse(intent).as[NineCardIntent],
        imagePath = imagePath)
    }

  def createSeqCardResponse(
    num: Int = 3,
    id: Int = cardId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: CardType = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification) =
    (0 until 3) map (item =>
      Card(
        id = id,
        position = position,
        term = term,
        packageName = Option(packageName),
        cardType = cardType,
        intent = Json.parse(intent).as[NineCardIntent],
        imagePath = imagePath,
        notification = Option(notification)))

  def updatedCard = Card(
    id = cardId,
    position = position,
    term = name,
    packageName = Option(packageName),
    cardType = cardType,
    intent = Json.parse(intent).as[NineCardIntent],
    imagePath = imagePath,
    notification = Option(notification))
}
