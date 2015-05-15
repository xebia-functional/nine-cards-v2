package com.fortysevendeg.repository.collection

import com.fortysevendeg.ninecardslauncher.commons.CollectionUri
import com.fortysevendeg.ninecardslauncher.provider.CollectionEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils._
import com.fortysevendeg.ninecardslauncher.provider.{CollectionEntity, CollectionEntityData, NineCardsSqlHelper}
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.{Collection, CollectionData}
import com.fortysevendeg.ninecardslauncher.repository.repositories.CollectionRepositoryClient
import com.fortysevendeg.repository._
import org.mockito.Mockito._
import org.specs2.specification.Scope

import scala.util.Random

trait CollectionTestSupport
    extends BaseTestSupport
    with CollectionRepositoryClient
    with CollectionTestData
    with MockContentResolverWrapper
    with Scope

trait CollectionTestData {
  val collectionId = Random.nextInt(10)
  val nonExistingCollectionId = 15
  val position = Random.nextInt(10)
  val nonExistingPosition = 15
  val name = Random.nextString(5)
  val `type` = Random.nextString(5)
  val icon = Random.nextString(5)
  val themedColorIndex = Random.nextInt(10)
  val appsCategory = Random.nextString(5)
  val constrains = Random.nextString(5)
  val originalSharedCollectionId = Random.nextString(5)
  val sharedCollectionIdInt = Random.nextInt(10)
  val nonExistingSharedCollectionIdInt = 15
  val sharedCollectionId = sharedCollectionIdInt.toString
  val nonExistingSharedCollectionId = nonExistingSharedCollectionIdInt.toString
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
      `type` = `type`,
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
      `type` = `type`,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = appsCategoryOption,
      constrains = constrainsOption,
      originalSharedCollectionId = originalSharedCollectionIdOption,
      sharedCollectionId = sharedCollectionIdOption,
      sharedCollectionSubscribed = sharedCollectionSubscribedOption)))

  def createCollectionValues = Map[String, Any](
    Position -> position,
    Name -> name,
    Type -> `type`,
    Icon -> icon,
    ThemedColorIndex -> themedColorIndex,
    AppsCategory -> (appsCategoryOption getOrElse ""),
    Constrains -> (constrainsOption getOrElse ""),
    OriginalSharedCollectionId -> (originalSharedCollectionIdOption getOrElse ""),
    SharedCollectionId -> (sharedCollectionIdOption getOrElse ""),
    SharedCollectionSubscribed -> (sharedCollectionSubscribedOption getOrElse false))
}

trait CollectionMockCursor extends MockCursor with CollectionTestData with Scope {

  val cursorData = Seq(
    (NineCardsSqlHelper.Id, 0, collectionSeq map (_.id), IntDataType),
    (Position, 1, collectionSeq map (_.data.position), IntDataType),
    (Name, 2, collectionSeq map (_.data.name), StringDataType),
    (Type, 3, collectionSeq map (_.data.`type`), StringDataType),
    (Icon, 4, collectionSeq map (_.data.icon), StringDataType),
    (ThemedColorIndex, 5, collectionSeq map (_.data.themedColorIndex), IntDataType),
    (AppsCategory, 6, collectionSeq map (_.data.appsCategory getOrElse ""), StringDataType),
    (Constrains, 7, collectionSeq map (_.data.constrains getOrElse ""), StringDataType),
    (OriginalSharedCollectionId, 8, collectionSeq map (_.data.originalSharedCollectionId getOrElse ""), StringDataType),
    (SharedCollectionId, 9, collectionSeq map (_.data.sharedCollectionId getOrElse ""), StringDataType),
    (SharedCollectionSubscribed, 10, collectionSeq map (item => if (item.data.sharedCollectionSubscribed getOrElse false) 1 else 0), IntDataType)
  )

  prepareCursor[Collection](collectionSeq.size, cursorData)
}

trait EmptyCollectionMockCursor extends MockCursor with CollectionTestData with Scope {

  val cursorData = Seq(
    (NineCardsSqlHelper.Id, 0, Seq.empty, IntDataType),
    (Position, 1, Seq.empty, IntDataType),
    (Name, 2, Seq.empty, StringDataType),
    (Type, 3, Seq.empty, StringDataType),
    (Icon, 4, Seq.empty, StringDataType),
    (ThemedColorIndex, 5, Seq.empty, IntDataType),
    (AppsCategory, 6, Seq.empty, StringDataType),
    (Constrains, 7, Seq.empty, StringDataType),
    (OriginalSharedCollectionId, 8, Seq.empty, StringDataType),
    (SharedCollectionId, 9, Seq.empty, StringDataType),
    (SharedCollectionSubscribed, 10, Seq.empty, IntDataType)
  )

  prepareCursor[Collection](0, cursorData)
}

trait AddCollectionSupport extends CollectionTestSupport {

  def createAddCollectionRequest = AddCollectionRequest(CollectionData(
    position = position,
    name = name,
    `type` = `type`,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = appsCategoryOption,
    constrains = constrainsOption,
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribedOption))

  when(contentResolverWrapper.insert(CollectionUri, createCollectionValues)).thenReturn(collectionId)
}

trait DeleteCollectionSupport extends CollectionTestSupport {

  def createDeleteCollectionRequest = DeleteCollectionRequest(collection = collection)

  when(contentResolverWrapper.deleteById(CollectionUri, collectionId)).thenReturn(1)
}

trait FindCollectionByIdSupport extends CollectionTestSupport {

  def createFindCollectionByIdRequest(id: Int) = FindCollectionByIdRequest(id = id)

  when(contentResolverWrapper.findById(
    nineCardsUri = CollectionUri,
    id = collectionId,
    projection = AllFields,
    where = "",
    whereParams = Seq.empty,
    orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(Some(collectionEntity))

  when(contentResolverWrapper.findById(
    nineCardsUri = CollectionUri,
    id = nonExistingCollectionId,
    projection = AllFields,
    where = "",
    whereParams = Seq.empty,
    orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(None)
}

trait FetchCollectionByOriginalSharedCollectionIdSupport extends CollectionTestSupport {

  def createFetchCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId: Int) = FetchCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId = sharedCollectionId)

  when(contentResolverWrapper.fetch(
    nineCardsUri = CollectionUri,
    projection = AllFields,
    where = s"$OriginalSharedCollectionId = ?",
    whereParams = Seq(sharedCollectionId),
    orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(Some(collectionEntity))

  when(contentResolverWrapper.fetch(
    nineCardsUri = CollectionUri,
    projection = AllFields,
    where = s"$OriginalSharedCollectionId = ?",
    whereParams = Seq(nonExistingSharedCollectionId),
    orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(None)
}

trait FetchCollectionByPositionSupport extends CollectionTestSupport {

  def createFetchCollectionByPositionRequest(position: Int) = FetchCollectionByPositionRequest(position = position)

  when(contentResolverWrapper.fetch(
    nineCardsUri = CollectionUri,
    projection = AllFields,
    where = s"$Position = ?",
    whereParams = Seq(position.toString),
    orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(Some(collectionEntity))

  when(contentResolverWrapper.fetch(
    nineCardsUri = CollectionUri,
    projection = AllFields,
    where = s"$Position = ?",
    whereParams = Seq(nonExistingPosition.toString),
    orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(None)
}

trait FetchSortedCollectionsSupport extends CollectionTestSupport {

  def createFetchSortedCollectionsRequest = FetchSortedCollectionsRequest()

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = CollectionUri,
    projection = AllFields,
    where = "",
    whereParams = Seq.empty,
    orderBy = s"$Position asc")(
        f = getListFromCursor(collectionEntityFromCursor))).thenReturn(collectionEntitySeq)
}

trait UpdateCollectionSupport extends CollectionTestSupport {

  def createUpdateCollectionRequest = UpdateCollectionRequest(collection = collection)

  when(contentResolverWrapper.updateById(CollectionUri, collectionId, createCollectionValues)).thenReturn(1)
}