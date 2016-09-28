package cards.nine.process.sharedcollections.impl

import cards.nine.process.commons.types.{AppsCollectionType, Communication}
import cards.nine.process.sharedcollections.TopSharedCollection
import cards.nine.process.sharedcollections.models.{CreateSharedCollection, UpdateSharedCollection}
import cards.nine.services.api._
import cards.nine.services.persistence.models.{Collection => CollectionPersistence}

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

  val sharedCollectionResponseList = SharedCollectionResponseList(
    statusCode = statusCodeOk,
    items = generateSharedCollectionSeq())

  val sharedCollectionResponse = SharedCollectionResponse(
    statusCode = statusCodeOk,
    sharedCollection = sharedCollectionResponseList.items.head)

  val sharedCollectionId = "shared-collection-id"

  def generateCreateSharedCollection =
    CreateSharedCollection(
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

  val updateSharedCollectionResponse =
    UpdateSharedCollectionResponse(
      statusCode = statusCodeOk,
      sharedCollectionId = sharedCollectionId)

  def generateUpdateSharedCollection =
    UpdateSharedCollection(
      sharedCollectionId,
      name = Random.nextString(10),
      packages = Seq.empty)

  val updateSharedCollection = generateUpdateSharedCollection

  def generateSharedCollectionId() =
    sharedCollectionId + Random.nextInt(10)

  def generateSubscriptionResponse() = 1 to 10 map { i =>
    SubscriptionResponse(sharedCollectionId = generateSharedCollectionId())
  }

  val subscriptionList = SubscriptionResponseList(
    statusCode = statusCodeOk,
    items = generateSubscriptionResponse())

  def generateSharedCollection() = 1 to 10 map { i =>
    SharedCollection(
      id = i.toString,
      sharedCollectionId = generateSharedCollectionId(),
      publishedOn = 0l,
      author = Random.nextString(10),
      name = Random.nextString(10),
      packages = Seq.empty,
      resolvedPackages = Seq.empty,
      views = Random.nextInt(),
      subscriptions = Some(Random.nextInt()),
      category = Random.nextString(10),
      icon = Random.nextString(10),
      community = false)
  }

  val publicationList = SharedCollectionResponseList(
    statusCode = statusCodeOk,
    items = generateSharedCollection())

  val publicationListIds = publicationList.items.map(_.sharedCollectionId)

  def generateOptionOriginalSharedCollectionId() =
    Random.nextBoolean() match {
      case true => None
      case false => Some(generateSharedCollectionId())
    }

  def generateCollection() = 1 to 10 map { i =>
    CollectionPersistence(
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
  val collection = collectionList(0)

  val publicCollectionList =
    collectionList.flatMap(collection => collection.originalSharedCollectionId.map((_, collection))).filter{
      case (sharedCollectionId: String, _) => !publicationListIds.contains(sharedCollectionId)
    }

  val subscribeResponse =
    SubscribeResponse(
      statusCode = statusCodeOk)

  val unsubscribeResponse =
    UnsubscribeResponse(
      statusCode = statusCodeOk)

  def collectionPersistencePublishedByMeSeq = sharedCollectionResponseList.items.map { col =>
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

  def collectionPersistencePublishedByOtherSeq = sharedCollectionResponseList.items.map { col =>
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

  def collectionPersistenceNotPublishedSeq = sharedCollectionResponseList.items.map { col =>
    CollectionPersistence(
      id = Random.nextInt(),
      position = Random.nextInt(10),
      name = col.name,
      collectionType = AppsCollectionType.name,
      icon = col.icon,
      themedColorIndex = 0,
      appsCategory = Some(col.category),
      originalSharedCollectionId = None,
      sharedCollectionId = None,
      sharedCollectionSubscribed = false,
      cards = Seq.empty,
      moment = None)
  }

  def collectionPersistenceSubscribedSeq = sharedCollectionResponseList.items.map { col =>
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
      sharedCollectionSubscribed = true,
      cards = Seq.empty,
      moment = None)
  }
}
