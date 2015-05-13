package com.fortysevendeg.repository

import com.fortysevendeg.ninecardslauncher.commons.CollectionUri
import com.fortysevendeg.ninecardslauncher.provider.CollectionEntity._
import com.fortysevendeg.ninecardslauncher.provider._
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.{Collection, CollectionData}
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Random

trait CollectionMockCursor extends MockCursor with CollectionTestData {

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

trait EmptyCollectionMockCursor extends MockCursor with CollectionTestData {

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

trait CollectionTestSupport
    extends BaseTestSupport
    with MockContentResolverWrapper
    with CollectionTestData
    with DBUtils {

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

  def createDeleteCollectionRequest = DeleteCollectionRequest(collection = collection)

  def createGetCollectionByIdRequest(id: Int) = GetCollectionByIdRequest(id = id)

  def createGetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId: Int) = GetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId = sharedCollectionId)

  def createGetCollectionByPositionRequest(position: Int) = GetCollectionByPositionRequest(position = position)

  def createGetSortedCollectionsRequest = GetSortedCollectionsRequest()

  def createUpdateCollectionRequest = UpdateCollectionRequest(collection = collection)

  when(contentResolverWrapper.insert(CollectionUri, createCollectionValues)).thenReturn(collectionId)

  when(contentResolverWrapper.deleteById(CollectionUri, collectionId)).thenReturn(1)

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

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = CollectionUri,
    projection = AllFields,
    where = "",
    whereParams = Seq.empty,
    orderBy = s"$Position asc")(
        f = getListFromCursor(collectionEntityFromCursor))).thenReturn(collectionEntitySeq)

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

  when(contentResolverWrapper.update(CollectionUri, createCollectionValues)).thenReturn(1)
}

class CollectionRepositoryClientSpec
    extends Specification
    with Mockito
    with Scope
    with CollectionTestSupport
    with CollectionRepositoryClient {

  "CollectionRepositoryClient component" should {

    "addCollection should return a valid Collection object" in {

      val response = await(addCollection(createAddCollectionRequest))

      response.collection.get.id shouldEqual collectionId
      response.collection.get.data.name shouldEqual name
    }

    "deleteCollection should return a successful response when a valid cache category id is given" in {
      val response = await(deleteCollection(createDeleteCollectionRequest))

      response.success shouldEqual true
    }

    "getCollectionById should return a Collection object when a existing id is given" in {
      val response = await(getCollectionById(createGetCollectionByIdRequest(id = collectionId)))

      response.result.get.id shouldEqual collectionId
      response.result.get.data.name shouldEqual name
    }

    "getCollectionById should return None when a non-existing id is given" in {
      val response = await(getCollectionById(createGetCollectionByIdRequest(id = nonExistingCollectionId)))

      response.result shouldEqual None
    }

    "getCollectionByOriginalSharedCollectionId should return a Collection object when a existing shared collection id is given" in {
      val response = await(getCollectionByOriginalSharedCollectionId(createGetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId = sharedCollectionIdInt)))

      response.result.get.id shouldEqual collectionId
      response.result.get.data.name shouldEqual name
    }

    "getCollectionByOriginalSharedCollectionId should return None when a non-existing shared collection id is given" in {
      val response = await(getCollectionByOriginalSharedCollectionId(createGetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId = nonExistingSharedCollectionIdInt)))

      response.result shouldEqual None
    }

    "getCollectionByPosition should return a Collection object when a existing position is given" in {
      val response = await(getCollectionByPosition(createGetCollectionByPositionRequest(position = position)))

      response.result.get.id shouldEqual collectionId
      response.result.get.data.name shouldEqual name
    }

    "getCollectionByPosition should return None when a non-existing position is given" in {
      val response = await(getCollectionByPosition(createGetCollectionByPositionRequest(position = nonExistingPosition)))

      response.result shouldEqual None
    }

    "getSortedCollections should return all the cache categories stored in the database" in {
      val response = await(getSortedCollections(createGetSortedCollectionsRequest))

      response.collections shouldEqual collectionSeq
    }

    "updateCollection should return a successful response when the collection is updated" in {
      val response = await(updateCollection(createUpdateCollectionRequest))

      response.success shouldEqual true
    }

    "getEntityFromCursor should return None when an empty cursor is given" in
        new EmptyCollectionMockCursor
            with Scope {
          val result = getEntityFromCursor(collectionEntityFromCursor)(mockCursor)

          result shouldEqual None
        }

    "getEntityFromCursor should return a Collection object when a cursor with data is given" in
        new CollectionMockCursor
            with Scope {
          val result = getEntityFromCursor(collectionEntityFromCursor)(mockCursor)

          result shouldEqual Some(collectionEntity)
        }

    "getListFromCursor should return an empty sequence when an empty cursor is given" in
        new EmptyCollectionMockCursor
            with Scope {
          val result = getListFromCursor(collectionEntityFromCursor)(mockCursor)

          result shouldEqual Seq.empty
        }

    "getEntityFromCursor should return a Collection sequence when a cursor with data is given" in
        new CollectionMockCursor
            with Scope {
          val result = getListFromCursor(collectionEntityFromCursor)(mockCursor)

          result shouldEqual collectionEntitySeq
        }
  }
}
