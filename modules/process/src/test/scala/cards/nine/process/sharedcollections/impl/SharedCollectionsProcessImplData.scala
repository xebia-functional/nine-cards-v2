package cards.nine.process.sharedcollections.impl

import cards.nine.models.{RequestConfig, Collection}
import cards.nine.models.types._
import TopSharedCollection

import scala.util.Random

trait SharedCollectionsProcessImplData {

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


  val sharedCollectionId = "shared-collection-id"



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
      collectionType = AppsCollectionType,
      icon = Random.nextString(10),
      themedColorIndex = Random.nextInt(10),
      appsCategory = None,
      originalSharedCollectionId = None,
      sharedCollectionId = generateOptionOriginalSharedCollectionId(),
      sharedCollectionSubscribed = false,
      cards = Seq.empty,
      moment = None,
      publicCollectionStatus = NotPublished)
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
    Collection(
      id = Random.nextInt(),
      position = Random.nextInt(10),
      name = col.name,
      collectionType = AppsCollectionType,
      icon = col.icon,
      themedColorIndex = 0,
      appsCategory = Some(NineCardsCategory(col.category)),
      originalSharedCollectionId = None,
      sharedCollectionId = Some(col.sharedCollectionId),
      sharedCollectionSubscribed = false,
      cards = Seq.empty,
      moment = None,
      publicCollectionStatus = PublishedByMe)
  }

  def collectionPersistencePublishedByOtherSeq = sharedCollectionResponseList.items.map { col =>
    Collection(
      id = Random.nextInt(),
      position = Random.nextInt(10),
      name = col.name,
      collectionType = AppsCollectionType,
      icon = col.icon,
      themedColorIndex = 0,
      appsCategory = Some(NineCardsCategory(col.category)),
      originalSharedCollectionId = Some(col.sharedCollectionId),
      sharedCollectionId = Some(col.sharedCollectionId),
      sharedCollectionSubscribed = false,
      cards = Seq.empty,
      moment = None,
      publicCollectionStatus = PublishedByOther)
  }

  def collectionPersistenceNotPublishedSeq = sharedCollectionResponseList.items.map { col =>
    Collection(
      id = Random.nextInt(),
      position = Random.nextInt(10),
      name = col.name,
      collectionType = AppsCollectionType,
      icon = col.icon,
      themedColorIndex = 0,
      appsCategory = Some(NineCardsCategory(col.category)),
      originalSharedCollectionId = None,
      sharedCollectionId = None,
      sharedCollectionSubscribed = false,
      cards = Seq.empty,
      moment = None,
      publicCollectionStatus = NotPublished)
  }

  def collectionPersistenceSubscribedSeq = sharedCollectionResponseList.items.map { col =>
    Collection(
      id = Random.nextInt(),
      position = Random.nextInt(10),
      name = col.name,
      collectionType = AppsCollectionType,
      icon = col.icon,
      themedColorIndex = 0,
      appsCategory = Some(NineCardsCategory(col.category)),
      originalSharedCollectionId = Some(col.sharedCollectionId),
      sharedCollectionId = Some(col.sharedCollectionId),
      sharedCollectionSubscribed = true,
      cards = Seq.empty,
      moment = None,
      publicCollectionStatus = NotPublished)
  }
}
