package com.fortysevendeg.repository.collection

import com.fortysevendeg.ninecardslauncher.repository.model.{CollectionData, Collection}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CollectionEntityData, CollectionEntity}

import scala.util.Random

trait CollectionRepositoryTestData {

  val collectionId = Random.nextInt(10)
  val nonExistingCollectionId = 15
  val position = Random.nextInt(10)
  val nonExistingPosition = 15
  val name = Random.nextString(5)
  val collectionType = Random.nextString(5)
  val icon = Random.nextString(5)
  val themedColorIndex = Random.nextInt(10)
  val appsCategory = Random.nextString(5)
  val constrains = Random.nextString(5)
  val originalSharedCollectionId = Random.nextString(5)
  val sharedCollectionId = Random.nextString(5)
  val nonExistingSharedCollectionId = Random.nextString(5)
  val sharedCollectionSubscribed = Random.nextInt(10) < 5
  val appsCategoryOption = Option(appsCategory)
  val constrainsOption = Option(constrains)
  val originalSharedCollectionIdOption = Option(originalSharedCollectionId)
  val sharedCollectionIdOption = Option(sharedCollectionId)
  val sharedCollectionSubscribedOption = Option(sharedCollectionSubscribed)

  val collectionEntitySeq = createCollectionEntitySeq(5)
  val collectionEntity = collectionEntitySeq.head
  val collectionSeq = createCollectionSeq(5)
  val collection = collectionSeq.head

  def createCollectionEntitySeq(num: Int) = (0 until num) map (i => CollectionEntity(
    id = collectionId + i,
    data = CollectionEntityData(
      position = position,
      name = name,
      `type` = collectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = appsCategory,
      constrains = constrains,
      originalSharedCollectionId = originalSharedCollectionId,
      sharedCollectionId = sharedCollectionId,
      sharedCollectionSubscribed = sharedCollectionSubscribed)))

  def createCollectionSeq(num: Int) = (0 until num) map (i => Collection(
    id = collectionId + i,
    data = CollectionData(
      position = position,
      name = name,
      collectionType = collectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = appsCategoryOption,
      constrains = constrainsOption,
      originalSharedCollectionId = originalSharedCollectionIdOption,
      sharedCollectionId = sharedCollectionIdOption,
      sharedCollectionSubscribed = sharedCollectionSubscribedOption)))

  def createCollectionValues = Map[String, Any](
    CollectionEntity.position -> position,
    CollectionEntity.name -> name,
    CollectionEntity.collectionType -> collectionType,
    CollectionEntity.icon -> icon,
    CollectionEntity.themedColorIndex -> themedColorIndex,
    CollectionEntity.appsCategory -> (appsCategoryOption getOrElse ""),
    CollectionEntity.constrains -> (constrainsOption getOrElse ""),
    CollectionEntity.originalSharedCollectionId -> (originalSharedCollectionIdOption getOrElse ""),
    CollectionEntity.sharedCollectionId -> (sharedCollectionIdOption getOrElse ""),
    CollectionEntity.sharedCollectionSubscribed -> (sharedCollectionSubscribedOption getOrElse false))

  def createCollectionData = CollectionData(
    position = position,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = appsCategoryOption,
    constrains = constrainsOption,
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribedOption)
}
