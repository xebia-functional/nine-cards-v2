package com.fortysevendeg.repository.collection

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.Collection
import com.fortysevendeg.ninecardslauncher.repository.provider.CollectionEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

trait CollectionRepositorySpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait CollectionRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val collectionRepository = new CollectionRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]
  }

  trait ValidCollectionRepositoryResponses
    extends CollectionRepositoryTestData {

    self: CollectionRepositoryScope =>

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createCollectionValues,
      notificationUri = Some(mockUri)) returns testCollectionId

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testCollectionId,
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testCollectionId,
      projection = allFields)(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns Some(collectionEntity)

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testNonExistingCollectionId,
      projection = allFields)(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns None

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = "",
      whereParams = Seq.empty,
      orderBy = s"$position asc")(
        f = getListFromCursor(collectionEntityFromCursor)) returns collectionEntitySeq

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$position = ?",
      whereParams = Seq(testPosition.toString),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns Some(collectionEntity)

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$position = ?",
      whereParams = Seq(testNonExistingPosition.toString),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns None

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$originalSharedCollectionId = ?",
      whereParams = Seq(testSharedCollectionId),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns Some(collectionEntity)

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$originalSharedCollectionId = ?",
      whereParams = Seq(testNonExistingSharedCollectionId),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) returns None

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = testCollectionId,
      values = createCollectionValues,
      notificationUri = Some(mockUri)) returns 1
  }

  trait ErrorCollectionRepositoryResponses
    extends CollectionRepositoryTestData {

    self: CollectionRepositoryScope =>

    val contentResolverException = new RuntimeException("Irrelevant message")

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createCollectionValues,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testCollectionId,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testCollectionId,
      projection = allFields)(
        f = getEntityFromCursor(collectionEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = "",
      whereParams = Seq.empty,
      orderBy = s"$position asc")(
        f = getListFromCursor(collectionEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$position = ?",
      whereParams = Seq(testPosition.toString),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$originalSharedCollectionId = ?",
      whereParams = Seq(testSharedCollectionId),
      orderBy = "")(
        f = getEntityFromCursor(collectionEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = testCollectionId,
      values = createCollectionValues,
      notificationUri = Some(mockUri)) throws contentResolverException
  }

}

trait CollectionMockCursor
  extends MockCursor
  with CollectionRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, collectionSeq map (_.id), IntDataType),
    (position, 1, collectionSeq map (_.data.position), IntDataType),
    (name, 2, collectionSeq map (_.data.name), StringDataType),
    (collectionType, 3, collectionSeq map (_.data.collectionType), StringDataType),
    (icon, 4, collectionSeq map (_.data.icon), StringDataType),
    (themedColorIndex, 5, collectionSeq map (_.data.themedColorIndex), IntDataType),
    (appsCategory, 6, collectionSeq map (_.data.appsCategory orNull), StringDataType),
    (originalSharedCollectionId, 8, collectionSeq map (_.data.originalSharedCollectionId orNull), StringDataType),
    (sharedCollectionId, 9, collectionSeq map (_.data.sharedCollectionId orNull), StringDataType),
    (sharedCollectionSubscribed, 10, collectionSeq map (item => if (item.data.sharedCollectionSubscribed getOrElse false) 1 else 0), IntDataType)
  )

  prepareCursor[Collection](collectionSeq.size, cursorData)
}

trait EmptyCollectionMockCursor
  extends MockCursor
  with CollectionRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (position, 1, Seq.empty, IntDataType),
    (name, 2, Seq.empty, StringDataType),
    (collectionType, 3, Seq.empty, StringDataType),
    (icon, 4, Seq.empty, StringDataType),
    (themedColorIndex, 5, Seq.empty, IntDataType),
    (appsCategory, 6, Seq.empty, StringDataType),
    (originalSharedCollectionId, 8, Seq.empty, StringDataType),
    (sharedCollectionId, 9, Seq.empty, StringDataType),
    (sharedCollectionSubscribed, 10, Seq.empty, IntDataType)
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

          val result = collectionRepository.addCollection(data = createCollectionData).run.run

          result must beLike {
            case Answer(collection) =>
              collection.id shouldEqual testCollectionId
              collection.data.name shouldEqual testName
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.addCollection(data = createCollectionData).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteCollections" should {

      "return a successful result when all collections are deleted" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.deleteCollections().run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.deleteCollections().run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteCollection" should {

      "return a successful result when a valid collection id is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.deleteCollection(collection).run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.deleteCollection(collection).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "findCollectionById" should {

      "return a Collection object when a existing id is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.findCollectionById(id = testCollectionId).run.run

          result must beLike {
            case Answer(maybeCollection) =>
              maybeCollection must beSome[Collection].which { collection =>
                collection.id shouldEqual testCollectionId
                collection.data.name shouldEqual testName
              }
          }
        }

      "return None when a non-existing id is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {
          val result = collectionRepository.findCollectionById(id = testNonExistingCollectionId).run.run

          result must beLike {
            case Answer(maybeCollection) =>
              maybeCollection must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.findCollectionById(id = testCollectionId).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "fetchCollectionBySharedCollectionId" should {

      "return a Collection object when a existing shared collection id is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId = testSharedCollectionId).run.run

          result must beLike {
            case Answer(maybeCollection) =>
              maybeCollection must beSome[Collection].which { collection =>
                collection.id shouldEqual testCollectionId
                collection.data.name shouldEqual testName
              }
          }
        }

      "return None when a non-existing shared collection id is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId = testNonExistingSharedCollectionId).run.run

          result must beLike {
            case Answer(maybeCollection) =>
              maybeCollection must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId = testSharedCollectionId).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "fetchCollectionByPosition" should {

      "return a Collection object when a existing position is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.fetchCollectionByPosition(position = testPosition).run.run

          result must beLike {
            case Answer(maybeCollection) =>
              maybeCollection must beSome[Collection].which { collection =>
                collection.id shouldEqual testCollectionId
                collection.data.position shouldEqual testPosition
              }
          }
        }

      "return None when a non-existing position is given" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {
          val result = collectionRepository.fetchCollectionByPosition(position = testNonExistingPosition).run.run

          result must beLike {
            case Answer(maybeCollection) =>
              maybeCollection must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.fetchCollectionByPosition(position = testPosition).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "fetchSortedCollections" should {

      "return all the cache categories stored in the database" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.fetchSortedCollections.run.run

          result must beLike {
            case Answer(collections) =>
              collections shouldEqual collectionSeq
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.fetchSortedCollections.run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "updateCollection" should {

      "return a successful result when the collection is updated" in
        new CollectionRepositoryScope
          with ValidCollectionRepositoryResponses {

          val result = collectionRepository.updateCollection(collection = collection).run.run

          result must beLike {
            case Answer(updated) =>
              updated shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new CollectionRepositoryScope
          with ErrorCollectionRepositoryResponses {

          val result = collectionRepository.updateCollection(collection = collection).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
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

          result should beEmpty
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
