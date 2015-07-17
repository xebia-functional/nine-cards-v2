package com.fortysevendeg.repository.collection

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.repository.commons.{CollectionUri, ContentResolverWrapperImpl}
import com.fortysevendeg.ninecardslauncher.repository.model.Collection
import com.fortysevendeg.ninecardslauncher.repository.provider.CollectionEntity.collectionEntityFromCursor
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait CollectionRepositorySpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait CollectionRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]
    lazy val collectionRepository = new CollectionRepository(contentResolverWrapper)
  }

  trait ValidCollectionRepositoryResponses
    extends DBUtils
    with CollectionRepositoryTestData {

    self: CollectionRepositoryScope =>

    contentResolverWrapper.insert(CollectionUri, createCollectionValues) returns collectionId

    contentResolverWrapper.deleteById(CollectionUri, collectionId) returns 1

    contentResolverWrapper.findById(
      nineCardsUri = CollectionUri,
      id = collectionId,
      projection = CollectionEntity.allFields)(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns Some(collectionEntity)

    contentResolverWrapper.findById(
      nineCardsUri = CollectionUri,
      id = nonExistingCollectionId,
      projection = CollectionEntity.allFields)(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns None

    contentResolverWrapper.fetchAll(
      nineCardsUri = CollectionUri,
      projection = CollectionEntity.allFields,
      where = "",
      whereParams = Seq.empty,
      orderBy = s"${CollectionEntity.position} asc")(
        f = getListFromCursor(collectionEntityFromCursor)) returns collectionEntitySeq

    contentResolverWrapper.fetch(
      nineCardsUri = CollectionUri,
      projection = CollectionEntity.allFields,
      where = s"${CollectionEntity.position} = ?",
      whereParams = Seq(position.toString),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns Some(collectionEntity)

    contentResolverWrapper.fetch(
      nineCardsUri = CollectionUri,
      projection = CollectionEntity.allFields,
      where = s"${CollectionEntity.position} = ?",
      whereParams = Seq(nonExistingPosition.toString),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns None

    contentResolverWrapper.fetch(
      nineCardsUri = CollectionUri,
      projection = CollectionEntity.allFields,
      where = s"${CollectionEntity.originalSharedCollectionId} = ?",
      whereParams = Seq(sharedCollectionId),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns Some(collectionEntity)

    contentResolverWrapper.fetch(
      nineCardsUri = CollectionUri,
      projection = CollectionEntity.allFields,
      where = s"${CollectionEntity.originalSharedCollectionId} = ?",
      whereParams = Seq(nonExistingSharedCollectionId),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns None

    contentResolverWrapper.updateById(CollectionUri, collectionId, createCollectionValues) returns 1
  }

  trait ErrorCollectionRepositoryResponses
    extends DBUtils
    with CollectionRepositoryTestData {

    self: CollectionRepositoryScope =>

    val contentResolverException = new RuntimeException("Irrelevant message")

    contentResolverWrapper.insert(CollectionUri, createCollectionValues) throws contentResolverException

    contentResolverWrapper.deleteById(CollectionUri, collectionId) throws contentResolverException

    contentResolverWrapper.findById(
      nineCardsUri = CollectionUri,
      id = collectionId,
      projection = CollectionEntity.allFields)(
        f = getEntityFromCursor(collectionEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      nineCardsUri = CollectionUri,
      projection = CollectionEntity.allFields,
      where = "",
      whereParams = Seq.empty,
      orderBy = s"${CollectionEntity.position} asc")(
        f = getListFromCursor(collectionEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      nineCardsUri = CollectionUri,
      projection = CollectionEntity.allFields,
      where = s"${CollectionEntity.position} = ?",
      whereParams = Seq(position.toString),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      nineCardsUri = CollectionUri,
      projection = CollectionEntity.allFields,
      where = s"${CollectionEntity.originalSharedCollectionId} = ?",
      whereParams = Seq(sharedCollectionId),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(CollectionUri, collectionId, createCollectionValues) throws contentResolverException
  }
}

trait CollectionMockCursor
  extends MockCursor
  with DBUtils
  with CollectionRepositoryTestData {

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

trait EmptyCollectionMockCursor
  extends MockCursor
  with DBUtils
  with CollectionRepositoryTestData {

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

class CollectionRepositorySpec
  extends CollectionRepositorySpecification {

  "CollectionRepositoryClient component" should {

    "addCollection" should {

      "return a Collection object with a valid request" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.addCollection(data = createCollectionData).run

          result must be_\/-[Collection].which {
            collection =>
              collection.id shouldEqual collectionId
              collection.data.name shouldEqual name
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.addCollection(data = createCollectionData).run

          result must be_-\/[NineCardsException]
        }
    }

    "deleteCollection" should {

      "return a successful result when a valid cache category id is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.deleteCollection(collection).run

          result must be_\/-[Int].which {
            deleted =>
              deleted shouldEqual 1
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.deleteCollection(collection).run

          result must be_-\/[NineCardsException]
        }
    }

    "findCollectionById" should {

      "return a Collection object when a existing id is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.findCollectionById(id = collectionId).run

          result must be_\/-[Option[Collection]].which {
            maybeCollection =>
              maybeCollection must beSome[Collection].which { collection =>
                collection.id shouldEqual collectionId
                collection.data.name shouldEqual name
              }
          }
        }

      "return None when a non-existing id is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {
          val result = collectionRepository.findCollectionById(id = nonExistingCollectionId).run

          result must be_\/-[Option[Collection]].which {
            maybeCollection =>
              maybeCollection must beNone
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.findCollectionById(id = collectionId).run

          result must be_-\/[NineCardsException]
        }
    }

    "fetchCollectionBySharedCollectionId" should {

      "return a Collection object when a existing shared collection id is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId = sharedCollectionId).run

          result must be_\/-[Option[Collection]].which {
            maybeCollection =>
              maybeCollection must beSome[Collection].which { collection =>
                collection.id shouldEqual collectionId
                collection.data.name shouldEqual name
              }
          }
        }

      "return None when a non-existing shared collection id is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId = nonExistingSharedCollectionId).run

          result must be_\/-[Option[Collection]].which {
            maybeCollection =>
              maybeCollection must beNone
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId = sharedCollectionId).run

          result must be_-\/[NineCardsException]
        }
    }

    "fetchCollectionByPosition" should {

      "return a Collection object when a existing position is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.fetchCollectionByPosition(position = position).run

          result must be_\/-[Option[Collection]].which {
            maybeCollection =>
              maybeCollection must beSome[Collection].which { collection =>
                collection.id shouldEqual collectionId
                collection.data.position shouldEqual position
              }
          }
        }

      "return None when a non-existing position is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {
          val result = collectionRepository.fetchCollectionByPosition(position = nonExistingPosition).run

          result must be_\/-[Option[Collection]].which {
            maybeCollection =>
              maybeCollection must beNone
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.fetchCollectionByPosition(position = position).run

          result must be_-\/[NineCardsException]
        }
    }

    "fetchSortedCollections" should {

      "return all the cache categories stored in the database" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.fetchSortedCollections.run

          result must be_\/-[Seq[Collection]].which {
            collections =>
              collections shouldEqual collectionSeq
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.fetchSortedCollections.run

          result must be_-\/[NineCardsException]
        }
    }

    "updateCollection" should {

      "return a successful result when the collection is updated" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.updateCollection(collection = collection).run

          result must be_\/-[Int].which {
            updated =>
              updated shouldEqual 1
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.updateCollection(collection = collection).run

          result must be_-\/[NineCardsException]
        }
    }

    "getEntityFromCursor" should {

      "return None when an empty cursor is given" in
        new EmptyCollectionMockCursor
          with Scope {

          val result = getEntityFromCursor(collectionEntityFromCursor)(mockCursor)

          result must beNone
        }

      "return a Collection object when a cursor with data is given" in
        new CollectionMockCursor
          with Scope {

          val result = getEntityFromCursor(collectionEntityFromCursor)(mockCursor)

          result must beSome[CollectionEntity].which { collection =>
            collection.id shouldEqual collectionEntity.id
            collection.data shouldEqual collectionEntity.data
          }
        }
    }

    "getListFromCursor" should {

      "return an empty sequence when an empty cursor is given" in
        new EmptyCollectionMockCursor
          with Scope {

          val result = getListFromCursor(collectionEntityFromCursor)(mockCursor)

          result shouldEqual Seq.empty
        }

      "return a Collection sequence when a cursor with data is given" in
        new CollectionMockCursor
          with Scope {

          val result = getListFromCursor(collectionEntityFromCursor)(mockCursor)

          result shouldEqual collectionEntitySeq
        }
    }
  }
}
