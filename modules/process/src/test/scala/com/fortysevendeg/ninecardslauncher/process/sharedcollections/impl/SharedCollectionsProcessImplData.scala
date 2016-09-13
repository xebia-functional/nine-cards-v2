package com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl

import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppsCollectionType, Communication}
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.TopSharedCollection
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.CreateSharedCollection
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Collection => CollectionPersistence}

import scala.util.Random

trait SharedCollectionsProcessImplData {

  val requestConfig = RequestConfig("fake-api-key", "fake-session-token", "fake-android-id", Some("fake-market-token"))

  val category = Communication

  val typeShareCollection = TopSharedCollection

  val offset = 0

  val limit = 50

  val statusCodeOk = 200

  def generateSharedCollectionSeq() = 1 to 10 map { i =>
    SharedCollection(
      id = Random.nextString(10),
      sharedCollectionId = Random.nextString(10),
      publishedOn = Random.nextLong(),
      description = Random.nextString(10),
      icon = Random.nextString(10),
      author = Random.nextString(10),
      name = Random.nextString(10),
      packages = Seq.empty,
      resolvedPackages = Seq.empty,
      views = Random.nextInt(),
      subscriptions = Some(Random.nextInt()),
      category = Communication.name,
      community = Random.nextBoolean())
  }

  val shareCollectionList = SharedCollectionResponseList(
    statusCode = statusCodeOk,
    items = generateSharedCollectionSeq())

  val sharedCollectionId = Random.nextString(10)

  def generateCreateSharedCollection =
    CreateSharedCollection(
      description = Random.nextString(10),
      author = Random.nextString(10),
      name = Random.nextString(10),
      packages = Seq.empty,
      category = Communication,
      icon = Random.nextString(10),
      community = Random.nextBoolean())

  val createSharedCollection = generateCreateSharedCollection

  val createSharedCollectionResponse =
    CreateSharedCollectionResponse(
      statusCode = statusCodeOk,
      sharedCollectionId = sharedCollectionId)

  val originalSharedCollectionId = Random.nextString(30)

  def generateOriginalSharedCollectionId() =
    originalSharedCollectionId + Random.nextInt(10)

  def generateSubscriptionResponse() = 1 to 10 map { i =>
    SubscriptionResponse(
      originalSharedCollectionId = generateOriginalSharedCollectionId())
  }

  val subscriptionList = SubscriptionResponseList(
    statusCode = statusCodeOk,
    items = generateSubscriptionResponse())

  def generateOptionOriginalSharedCollectionId() =
    Random.nextBoolean() match {
      case true => None
      case false => Some(generateOriginalSharedCollectionId())
    }

  def generateCollection() = 1 to 10 map { i =>
    Collection(
      id = i,
      position = Random.nextInt(10),
      name = Random.nextString(10),
      collectionType = Random.nextString(10),
      icon = Random.nextString(10),
      themedColorIndex = Random.nextInt(10),
      appsCategory = None,
      originalSharedCollectionId = generateOptionOriginalSharedCollectionId(),
      sharedCollectionId = None,
      sharedCollectionSubscribed = false,
      cards = Seq.empty,
      moment = None)
  }

  val collectionList = generateCollection()

  val publicCollectionList = collectionList.filter(_.originalSharedCollectionId.isDefined)

  val subscribeResponse =
    SubscribeResponse(
      statusCode = statusCodeOk)

  val unsubscribeResponse =
    UnsubscribeResponse(
      statusCode = statusCodeOk)

  def collectionPersistenceOwnedSeq = shareCollectionList.items.map { col =>
    CollectionPersistence(
      id = Random.nextInt(),
      position = Random.nextInt(10),
      name = col.name,
      collectionType = AppsCollectionType.name,
      icon = col.icon,
      themedColorIndex = 0,
      appsCategory = Some(col.category),
      originalSharedCollectionId = None,
      sharedCollectionId = Some(col.sharedCollectionId),
      sharedCollectionSubscribed = false,
      cards = Seq.empty,
      moment = None)
  }

  def collectionPersistenceSubscribedSeq = shareCollectionList.items.map { col =>
    CollectionPersistence(
      id = Random.nextInt(),
      position = Random.nextInt(10),
      name = col.name,
      collectionType = AppsCollectionType.name,
      icon = col.icon,
      themedColorIndex = 0,
      appsCategory = Some(col.category),
      originalSharedCollectionId = Some(col.sharedCollectionId),
      sharedCollectionId = Some(col.sharedCollectionId),
      sharedCollectionSubscribed = false,
      cards = Seq.empty,
      moment = None)
  }
}
