package com.fortysevendeg.repository.collection

import com.fortysevendeg.ninecardslauncher.repository.model.{CollectionData, Collection}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CollectionEntityData, CollectionEntity}

import scala.util.Random

trait CollectionRepositoryTestData {

  val testCollectionId = Random.nextInt(10)
  val testNonExistingCollectionId = 15
  val testPosition = Random.nextInt(10)
  val testNonExistingPosition = 15
  val testName = Random.nextString(5)
  val testCollectionType = Random.nextString(5)
  val testIcon = Random.nextString(5)
  val testThemedColorIndex = Random.nextInt(10)
  val testAppsCategory = Random.nextString(5)
  val testOriginalSharedCollectionId = Random.nextString(5)
  val testSharedCollectionId = Random.nextString(5)
  val testNonExistingSharedCollectionId = Random.nextString(5)
  val testSharedCollectionSubscribed = Random.nextInt(10) < 5
  val testAppsCategoryOption = Option(testAppsCategory)
  val testOriginalSharedCollectionIdOption = Option(testOriginalSharedCollectionId)
  val testSharedCollectionIdOption = Option(testSharedCollectionId)
  val testSharedCollectionSubscribedOption = Option(testSharedCollectionSubscribed)

  val collectionEntitySeq = createCollectionEntitySeq(5)
  val collectionEntity = collectionEntitySeq(0)
  val collectionSeq = createCollectionSeq(5)
  val collection = collectionSeq(0)

  def createCollectionEntitySeq(num: Int) = List.tabulate(num)(
    i => CollectionEntity(
      id = testCollectionId + i,
      data = CollectionEntityData(
        position = testPosition,
        name = testName,
        `type` = testCollectionType,
        icon = testIcon,
        themedColorIndex = testThemedColorIndex,
        appsCategory = testAppsCategory,
        originalSharedCollectionId = testOriginalSharedCollectionId,
        sharedCollectionId = testSharedCollectionId,
        sharedCollectionSubscribed = testSharedCollectionSubscribed)))

  def createCollectionSeq(num: Int) = List.tabulate(num)(
    i => Collection(
      id = testCollectionId + i,
      data = CollectionData(
        position = testPosition,
        name = testName,
        collectionType = testCollectionType,
        icon = testIcon,
        themedColorIndex = testThemedColorIndex,
        appsCategory = testAppsCategoryOption,
        originalSharedCollectionId = testOriginalSharedCollectionIdOption,
        sharedCollectionId = testSharedCollectionIdOption,
        sharedCollectionSubscribed = testSharedCollectionSubscribedOption)))

  def createCollectionValues = Map[String, Any](
    CollectionEntity.position -> testPosition,
    CollectionEntity.name -> testName,
    CollectionEntity.collectionType -> testCollectionType,
    CollectionEntity.icon -> testIcon,
    CollectionEntity.themedColorIndex -> testThemedColorIndex,
    CollectionEntity.appsCategory -> (testAppsCategoryOption orNull),
    CollectionEntity.originalSharedCollectionId -> (testOriginalSharedCollectionIdOption orNull),
    CollectionEntity.sharedCollectionId -> (testSharedCollectionIdOption orNull),
    CollectionEntity.sharedCollectionSubscribed -> (testSharedCollectionSubscribedOption getOrElse false))

  def createCollectionData = CollectionData(
    position = testPosition,
    name = testName,
    collectionType = testCollectionType,
    icon = testIcon,
    themedColorIndex = testThemedColorIndex,
    appsCategory = testAppsCategoryOption,
    originalSharedCollectionId = testOriginalSharedCollectionIdOption,
    sharedCollectionId = testSharedCollectionIdOption,
    sharedCollectionSubscribed = testSharedCollectionSubscribedOption)
}
