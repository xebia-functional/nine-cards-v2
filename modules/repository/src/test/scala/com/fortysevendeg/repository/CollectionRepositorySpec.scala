package com.fortysevendeg.repository

import com.fortysevendeg.ninecardslauncher.commons.{ContentResolverWrapperImpl, CollectionUri}
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
    (Type, 3, collectionSeq map (_.data.collectionType), StringDataType),
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
      collectionType = `type`,
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
  with CollectionTestData
  with DBUtils
  with Mockito {

  lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]
  lazy val collectionRepository = new CollectionRepository(contentResolverWrapper)

  def createAddCollectionRequest = AddCollectionRequest(CollectionData(
    position = position,
    name = name,
    collectionType = `type`,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = appsCategoryOption,
    constrains = constrainsOption,
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribedOption))

  def createDeleteCollectionRequest = DeleteCollectionRequest(collection = collection)

  def createGetCollectionByIdRequest(id: Int) = FindCollectionByIdRequest(id = id)

  def createGetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId: Int) = FetchCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId = sharedCollectionId)

  def createGetCollectionByPositionRequest(position: Int) = FetchCollectionByPositionRequest(position = position)

  def createGetSortedCollectionsRequest = FetchSortedCollectionsRequest()

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

  when(contentResolverWrapper.updateById(CollectionUri, collectionId, createCollectionValues)).thenReturn(1)
}

class CollectionRepositorySpec
  extends Specification
  with Mockito
  with Scope
  with CollectionTestSupport {

  "CollectionRepositoryClient component" should {

    "addCollection should return a valid Collection object" in {

      val response = await(collectionRepository.addCollection(createAddCollectionRequest))

      response.collection.id shouldEqual collectionId
      response.collection.data.name shouldEqual name
    }

    "deleteCollection should return a successful response when a valid cache category id is given" in {
      val response = await(collectionRepository.deleteCollection(createDeleteCollectionRequest))

      response.deleted shouldEqual 1
    }

    "findCollectionById should return a Collection object when a existing id is given" in {
      val response = await(collectionRepository.findCollectionById(createGetCollectionByIdRequest(id = collectionId)))

      response.collection must beSome[Collection].which { collection =>
        collection.id shouldEqual collectionId
        collection.data.name shouldEqual name
      }
    }

    "findCollectionById should return None when a non-existing id is given" in {
      val response = await(collectionRepository.findCollectionById(createGetCollectionByIdRequest(id = nonExistingCollectionId)))

      response.collection must beNone
    }

    "fetchCollectionByOriginalSharedCollectionId should return a Collection object when a existing shared collection id is given" in {
      val response = await(collectionRepository.fetchCollectionByOriginalSharedCollectionId(createGetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId = sharedCollectionIdInt)))

      response.collection must beSome[Collection].which { collection =>
        collection.id shouldEqual collectionId
        collection.data.name shouldEqual name
      }
    }

    "fetchCollectionByOriginalSharedCollectionId should return None when a non-existing shared collection id is given" in {
      val response = await(collectionRepository.fetchCollectionByOriginalSharedCollectionId(createGetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId = nonExistingSharedCollectionIdInt)))

      response.collection must beNone
    }

    "fetchCollectionByPosition should return a Collection object when a existing position is given" in {
      val response = await(collectionRepository.fetchCollectionByPosition(createGetCollectionByPositionRequest(position = position)))

      response.collection must beSome[Collection].which { collection =>
        collection.id shouldEqual collectionId
        collection.data.position shouldEqual position
      }
    }

    "fetchCollectionByPosition should return None when a non-existing position is given" in {
      val response = await(collectionRepository.fetchCollectionByPosition(createGetCollectionByPositionRequest(position = nonExistingPosition)))

      response.collection must beNone
    }

    "fetchSortedCollections should return all the cache categories stored in the database" in {
      val response = await(collectionRepository.fetchSortedCollections(createGetSortedCollectionsRequest))

      response.collections shouldEqual collectionSeq
    }

    "updateCollection should return a successful response when the collection is updated" in {
      val response = await(collectionRepository.updateCollection(createUpdateCollectionRequest))

      response.updated shouldEqual 1
    }

    "getEntityFromCursor should return None when an empty cursor is given" in
      new EmptyCollectionMockCursor
        with Scope {
        val result = getEntityFromCursor(collectionEntityFromCursor)(mockCursor)

        result must beNone
      }

    "getEntityFromCursor should return a Collection object when a cursor with data is given" in
      new CollectionMockCursor
        with Scope {
        val result = getEntityFromCursor(collectionEntityFromCursor)(mockCursor)

        result must beSome[CollectionEntity].which { collection =>
          collection.id shouldEqual collectionEntity.id
          collection.data shouldEqual collectionEntity.data
        }
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
