package com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl

import com.fortysevendeg.ninecardslauncher.process.commons.types.Communication
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.TopSharedCollection
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.CreateSharedCollection
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection

import scala.util.Random

trait SharedCollectionsProcessImplData {

  val requestConfig = RequestConfig("fake-api-key", "fake-session-token", "fake-android-id", Some("fake-market-token"))

  val category = Communication

  val typeShareCollection = TopSharedCollection

  val offset = 0

  val limit = 50

  val statusCodeOk = 200

  def generateSharedCollectionResponse() = 1 to 10 map { i =>
    SharedCollectionResponse(
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
    items = generateSharedCollectionResponse())

  val sharedCollectionId = "shared-collection-id"

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

  def generateSharedCollectionId() =
    sharedCollectionId + Random.nextInt(10)

  def generateSubscriptionResponse() = 1 to 10 map { i =>
    SubscriptionResponse(sharedCollectionId = generateSharedCollectionId())
  }

  val subscriptionList = SubscriptionResponseList(
    statusCode = statusCodeOk,
    items = generateSubscriptionResponse())

  def generateOptionOriginalSharedCollectionId() =
    Random.nextBoolean() match {
      case true => None
      case false => Some(generateSharedCollectionId())
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
      originalSharedCollectionId = None,
      sharedCollectionId = generateOptionOriginalSharedCollectionId(),
      sharedCollectionSubscribed = false,
      cards = Seq.empty,
      moment = None)
  }

  val collectionList = generateCollection()

  val publicCollectionList = collectionList.filter(_.sharedCollectionId.isDefined)

  val subscribeResponse =
    SubscribeResponse(
      statusCode = statusCodeOk)

  val unsubscribeResponse =
    UnsubscribeResponse(
      statusCode = statusCodeOk)
}
