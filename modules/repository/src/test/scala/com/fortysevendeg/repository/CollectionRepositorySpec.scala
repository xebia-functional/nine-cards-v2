package com.fortysevendeg.repository

import com.fortysevendeg.ninecardslauncher.repository.commons.{CollectionUri, ContentResolverWrapperImpl}
import com.fortysevendeg.ninecardslauncher.repository.model.{Collection, CollectionData}
import com.fortysevendeg.ninecardslauncher.repository.provider.CollectionEntity.collectionEntityFromCursor
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import org.mockito.Mockito._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Random

trait CollectionMockCursor extends MockCursor with CollectionTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, collectionSeq map (_.id), IntDataType),
    (CollectionEntity.position, 1, collectionSeq map (_.data.position), IntDataType),
    (CollectionEntity.name, 2, collectionSeq map (_.data.name), StringDataType),
    (CollectionEntity.collectionType, 3, collectionSeq map (_.data.collectionType), StringDataType),
    (CollectionEntity.icon, 4, collectionSeq map (_.data.icon), StringDataType),
    (CollectionEntity.themedColorIndex, 5, collectionSeq map (_.data.themedColorIndex), IntDataType),
    (CollectionEntity.appsCategory, 6, collectionSeq map (_.data.appsCategory getOrElse ""), StringDataType),
    (CollectionEntity.constrains, 7, collectionSeq map (_.data.constrains getOrElse ""), StringDataType),
    (CollectionEntity.originalSharedCollectionId, 8, collectionSeq map (_.data.originalSharedCollectionId getOrElse ""), StringDataType),
    (CollectionEntity.sharedCollectionId, 9, collectionSeq map (_.data.sharedCollectionId getOrElse ""), StringDataType),
    (CollectionEntity.sharedCollectionSubscribed, 10, collectionSeq map (item => if (item.data.sharedCollectionSubscribed getOrElse false) 1 else 0), IntDataType)
  )

  prepareCursor[Collection](collectionSeq.size, cursorData)
}

trait EmptyCollectionMockCursor extends MockCursor with CollectionTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (CollectionEntity.position, 1, Seq.empty, IntDataType),
    (CollectionEntity.name, 2, Seq.empty, StringDataType),
    (CollectionEntity.collectionType, 3, Seq.empty, StringDataType),
    (CollectionEntity.icon, 4, Seq.empty, StringDataType),
    (CollectionEntity.themedColorIndex, 5, Seq.empty, IntDataType),
    (CollectionEntity.appsCategory, 6, Seq.empty, StringDataType),
    (CollectionEntity.constrains, 7, Seq.empty, StringDataType),
    (CollectionEntity.originalSharedCollectionId, 8, Seq.empty, StringDataType),
    (CollectionEntity.sharedCollectionId, 9, Seq.empty, StringDataType),
    (CollectionEntity.sharedCollectionSubscribed, 10, Seq.empty, IntDataType)
  )

  prepareCursor[Collection](0, cursorData)
}

trait CollectionTestData {
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
}

trait CollectionTestSupport
  extends CollectionTestData
  with DBUtils
  with Mockito {

  lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]
  lazy val collectionRepository = new CollectionRepository(contentResolverWrapper)

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

  when(contentResolverWrapper.insert(CollectionUri, createCollectionValues)).thenReturn(collectionId)

  when(contentResolverWrapper.deleteById(CollectionUri, collectionId)).thenReturn(1)

  when(contentResolverWrapper.findById(
    nineCardsUri = CollectionUri,
    id = collectionId,
    projection = CollectionEntity.allFields)(
      f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(Some(collectionEntity))

  when(contentResolverWrapper.findById(
    nineCardsUri = CollectionUri,
    id = nonExistingCollectionId,
    projection = CollectionEntity.allFields)(
      f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(None)

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = CollectionUri,
    projection = CollectionEntity.allFields,
    where = "",
    whereParams = Seq.empty,
    orderBy = s"${CollectionEntity.position} asc")(
      f = getListFromCursor(collectionEntityFromCursor))).thenReturn(collectionEntitySeq)

  when(contentResolverWrapper.fetch(
    nineCardsUri = CollectionUri,
    projection = CollectionEntity.allFields,
    where = s"${CollectionEntity.position} = ?",
    whereParams = Seq(position.toString),
    orderBy = "")(
      f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(Some(collectionEntity))

  when(contentResolverWrapper.fetch(
    nineCardsUri = CollectionUri,
    projection = CollectionEntity.allFields,
    where = s"${CollectionEntity.position} = ?",
    whereParams = Seq(nonExistingPosition.toString),
    orderBy = "")(
      f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(None)

  when(contentResolverWrapper.fetch(
    nineCardsUri = CollectionUri,
    projection = CollectionEntity.allFields,
    where = s"${CollectionEntity.originalSharedCollectionId} = ?",
    whereParams = Seq(sharedCollectionId),
    orderBy = "")(
      f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(Some(collectionEntity))

  when(contentResolverWrapper.fetch(
    nineCardsUri = CollectionUri,
    projection = CollectionEntity.allFields,
    where = s"${CollectionEntity.originalSharedCollectionId} = ?",
    whereParams = Seq(nonExistingSharedCollectionId),
    orderBy = "")(
      f = getEntityFromCursor(collectionEntityFromCursor))).thenReturn(None)

  when(contentResolverWrapper.updateById(CollectionUri, collectionId, createCollectionValues)).thenReturn(1)
}

class CollectionRepositorySpec
  extends Specification
  with Mockito
  with Scope
  with DisjunctionMatchers
  with CollectionTestSupport {

  "CollectionRepositoryClient component" should {

    "addCollection should return a valid Collection object" in {

      val result = collectionRepository.addCollection(data = createCollectionData).run

      result must be_\/-[Collection].which {
        collection =>
          collection.id shouldEqual collectionId
          collection.data.name shouldEqual name
      }
    }

    "deleteCollection should return a successful result when a valid cache category id is given" in {
      val result = collectionRepository.deleteCollection(collection).run

      result must be_\/-[Int].which {
        deleted =>
          deleted shouldEqual 1
      }
    }

    "findCollectionById should return a Collection object when a existing id is given" in {
      val result = collectionRepository.findCollectionById(id = collectionId).run

      result must be_\/-[Option[Collection]].which {
        maybeCollection =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.data.name shouldEqual name
          }
      }
    }

    "findCollectionById should return None when a non-existing id is given" in {
      val result = collectionRepository.findCollectionById(id = nonExistingCollectionId).run

      result must be_\/-[Option[Collection]].which {
        maybeCollection =>
          maybeCollection must beNone
      }
    }

    "fetchCollectionBySharedCollectionId should return a Collection object when a existing shared collection id is given" in {
      val result = collectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId = sharedCollectionIdInt).run

      result must be_\/-[Option[Collection]].which {
        maybeCollection =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.data.name shouldEqual name
          }
      }
    }

    "fetchCollectionBySharedCollectionId should return None when a non-existing shared collection id is given" in {
      val result = collectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId = nonExistingSharedCollectionIdInt).run

      result must be_\/-[Option[Collection]].which {
        maybeCollection =>
          maybeCollection must beNone
      }
    }

    "fetchCollectionByPosition should return a Collection object when a existing position is given" in {
      val result = collectionRepository.fetchCollectionByPosition(position = position).run

      result must be_\/-[Option[Collection]].which {
        maybeCollection =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.data.position shouldEqual position
          }
      }
    }

    "fetchCollectionByPosition should return None when a non-existing position is given" in {
      val result = collectionRepository.fetchCollectionByPosition(position = nonExistingPosition).run

      result must be_\/-[Option[Collection]].which {
        maybeCollection =>
          maybeCollection must beNone
      }
    }

    "fetchSortedCollections should return all the cache categories stored in the database" in {
      val result = collectionRepository.fetchSortedCollections.run

      result must be_\/-[Seq[Collection]].which {
        collections =>
          collections shouldEqual collectionSeq
      }

    }

    "updateCollection should return a successful result when the collection is updated" in {
      val result = collectionRepository.updateCollection(collection = collection).run

      result must be_\/-[Int].which {
        updated =>
          updated shouldEqual 1
      }
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
