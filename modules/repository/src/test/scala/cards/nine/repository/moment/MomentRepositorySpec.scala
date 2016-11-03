package cards.nine.repository.moment

import android.net.Uri
import cards.nine.commons.contentresolver.Conversions._
import cards.nine.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import cards.nine.commons.contentresolver.IterableCursor._
import cards.nine.commons.javaNull
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.repository.{IntDataType, MockCursor, StringDataType}
import cards.nine.repository.RepositoryException
import cards.nine.repository.model.Moment
import cards.nine.repository.provider.MomentEntity._
import cards.nine.repository.provider._
import cards.nine.repository.repositories.MomentRepository
import cards.nine.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait MomentRepositorySpecification
  extends Specification
    with DisjunctionMatchers
    with Mockito {

  trait MomentRepositoryScope
    extends Scope
    with MomentRepositoryTestData {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val momentRepository = new MomentRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]

    uriCreator.parse(any) returns mockUri

    val contentResolverException = new RuntimeException("Irrelevant message")
  }

}

trait MomentMockCursor
  extends MockCursor
    with MomentRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, momentSeq map (_.id), IntDataType),
    (collectionId, 1, momentSeq map (_.data.collectionId getOrElse (javaNull)), IntDataType),
    (timeslot, 2, momentSeq map (_.data.timeslot), StringDataType),
    (wifi, 4, momentSeq map (_.data.wifi), StringDataType),
    (headphone, 5, momentSeq map (item => if (item.data.headphone) 1 else 0), IntDataType),
    (momentType, 6, momentSeq map (_.data.momentType getOrElse (javaNull)), StringDataType))

  prepareCursor[Moment](momentSeq.size, cursorData)
}

trait EmptyMomentMockCursor
  extends MockCursor
    with MomentRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (collectionId, 1, Seq.empty, IntDataType),
    (timeslot, 2, Seq.empty, StringDataType),
    (wifi, 4, Seq.empty, StringDataType),
    (headphone, 5, Seq.empty, IntDataType),
    (momentType, 6, Seq.empty, StringDataType))

  prepareCursor[Moment](0, cursorData)
}

class MomentRepositorySpec
  extends MomentRepositorySpecification {

  "MomentRepositoryClient component" should {

    "addMoment" should {

      "return a Moment object with a valid request" in
        new MomentRepositoryScope {

          contentResolverWrapper.insert(any, any, any) returns testId
          val result = momentRepository.addMoment(data = createMomentData).value.run

          result must beLike {
            case Right(momentResult) =>
              momentResult.id shouldEqual testId
              momentResult.data.collectionId shouldEqual testCollectionIdOption
          }
        }

      "return a Moment object with a collectionId = None with a valid request with a collectionId = None" in
        new MomentRepositoryScope {

          contentResolverWrapper.insert(any, any, any) returns testId
          val result = momentRepository.addMoment(data = createMomentDataCollection).value.run

          result must beLike {
            case Right(momentResult) =>
              momentResult.id shouldEqual testId
              momentResult.data.collectionId shouldEqual None
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope {

          contentResolverWrapper.insert(any, any, any) throws contentResolverException
          val result = momentRepository.addMoment(data = createMomentData).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "addMoments" should {

      "return a sequence of Moment objects with a valid request" in
        new MomentRepositoryScope {

          contentResolverWrapper.inserts(any,any,any,any) returns momentIdSeq

          val result = momentRepository.addMoments(datas = momentDataSeq).value.run

          result must beLike{
            case Right(moments) =>
              moments map (_.data.wifi) shouldEqual (momentSeq map (_.data.wifi))
              moments map (_.id) shouldEqual momentIdSeq
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope {

          contentResolverWrapper.inserts(any, any, any, any) throws contentResolverException
          val result = momentRepository.addMoments(datas = momentDataSeq).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "deleteMoments" should {

      "return a successful result when all the moments are deleted" in
        new MomentRepositoryScope {

          contentResolverWrapper.delete(any, any, any, any) returns 1
          val result = momentRepository.deleteMoments().value.run
          result shouldEqual Right(1)
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope {

          contentResolverWrapper.delete(any, any, any, any) throws contentResolverException
          val result = momentRepository.deleteMoments().value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "deleteMoment" should {

      "return a successful result when a valid moment id is given" in
        new MomentRepositoryScope {

          contentResolverWrapper.deleteById(any, any, any, any, any) returns 1
          val result = momentRepository.deleteMoment(moment.id).value.run
          result shouldEqual Right(1)
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope {

          contentResolverWrapper.deleteById(any, any, any, any, any) throws contentResolverException
          val result = momentRepository.deleteMoment(moment.id).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "findMomentById" should {

      "return a Moment object when a existing id is given" in
        new MomentRepositoryScope {

          contentResolverWrapper.findById(
            uri = mockUri,
            id = testId,
            projection = allFields)(f = getEntityFromCursor(momentEntityFromCursor)) returns Some(momentEntity)

          val result = momentRepository.findMomentById(id = testId).value.run

          result must beLike {
            case Right(maybeMoment) =>
              maybeMoment must beSome[Moment].which { moment =>
                moment.id shouldEqual testId
                moment.data.collectionId shouldEqual testCollectionIdOption
              }
          }
        }

      "return None when a non-existing id is given" in
        new MomentRepositoryScope {

          contentResolverWrapper.findById(any, any, any, any, any, any)(any) returns None
          val result = momentRepository.findMomentById(id = testNonExistingId).value.run
          result shouldEqual Right(None)
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope {

          contentResolverWrapper.findById(any, any, any, any, any, any)(any) throws contentResolverException
          val result = momentRepository.findMomentById(id = testId).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchMomentByCollectionId" should {

      "return a Moment object when a existing collectionId is given" in
        new MomentRepositoryScope {

          contentResolverWrapper.fetch(
            uri = mockUri,
            projection = allFields,
            where = s"$collectionId = ?",
            whereParams = Seq(testCollectionId.toString),
            orderBy = "")(
            f = getEntityFromCursor(momentEntityFromCursor)) returns Some(momentEntity)
          val result = momentRepository.fetchMomentByCollectionId(collectionId = testCollectionId).value.run

          result must beLike {
            case Right(maybeMoment) =>
              maybeMoment must beSome[Moment].which { moment =>
                moment.id shouldEqual testId
                moment.data.collectionId shouldEqual testCollectionIdOption
              }
          }
        }

      "return None when a non-existing collectionId is given" in
        new MomentRepositoryScope {

          contentResolverWrapper.fetch(
            uri = mockUri,
            projection = allFields,
            where = s"$collectionId = ?",
            whereParams = Seq(testNonExistingCollectionId.toString),
            orderBy = "")(
            f = getEntityFromCursor(momentEntityFromCursor)) returns None
          val result = momentRepository.fetchMomentByCollectionId(collectionId = testNonExistingCollectionId).value.run
          result shouldEqual Right(None)
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope {

          contentResolverWrapper.fetch(
            uri = mockUri,
            projection = allFields,
            where = s"$collectionId = ?",
            whereParams = Seq(testCollectionId.toString),
            orderBy = "")(
            f = getEntityFromCursor(momentEntityFromCursor)) throws contentResolverException

          val result = momentRepository.fetchMomentByCollectionId(collectionId = testCollectionId).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "updateMoment" should {

      "return a successful result when the moment is updated" in
        new MomentRepositoryScope {

          contentResolverWrapper.updateById(any, any, any, any) returns 1
          val result = momentRepository.updateMoment(item = moment).value.run
          result shouldEqual Right(1)
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope {

          contentResolverWrapper.updateById(any, any, any, any) throws contentResolverException
          val result = momentRepository.updateMoment(item = moment).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchMoments" should {

      "return all Moments" in
        new MomentRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = "",
            whereParams = Seq.empty, orderBy = "")(f = getListFromCursor(momentEntityFromCursor)) returns momentEntitySeq

          val result = momentRepository.fetchMoments().value.run
          result shouldEqual Right(momentSeq)
        }

      "return all Moments that match the where clause" in
        new MomentRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = s"$wifi = ?",
            whereParams = Seq(testWifi.toString),
            orderBy = "")(f = getListFromCursor(momentEntityFromCursor)) returns momentEntitySeq

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = s"$wifi = ?",
            whereParams = Seq(testWifi.toString))(f = getListFromCursor(momentEntityFromCursor)) returns momentEntitySeq

          val result = momentRepository.fetchMoments(where = s"$wifi = ?", whereParams = Seq(testWifi.toString)).value.run
          result shouldEqual Right(momentSeq)

        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = "",
            whereParams = Seq.empty,
            orderBy = "")(
            f = getListFromCursor(momentEntityFromCursor)) throws contentResolverException

          val result = momentRepository.fetchMoments().value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchIterableMoments" should {

      "return an IterableCursor of Moment" in
        new MomentMockCursor with MomentRepositoryScope {

          contentResolverWrapper.getCursor(any, any, any, any, any) returns mockCursor

          val result = momentRepository.fetchIterableMoments(where = testMockWhere).value.run

          result must beLike {
            case Right(iterator) =>
              toSeq(iterator) shouldEqual momentSeq
          }

          there was one(contentResolverWrapper).getCursor(
            mockUri,
            AppEntity.allFields,
            testMockWhere,
            Seq.empty,
            "")
        }

      "return an a RepositoryException when a exception is thrown " in
        new MomentMockCursor with MomentRepositoryScope {

          contentResolverWrapper.getCursor(any, any, any, any, any) throws contentResolverException

          val result = momentRepository.fetchIterableMoments(where = testMockWhere).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "getEntityFromCursor" should {

      "return None when an empty cursor is given" in
        new EmptyMomentMockCursor
          with Scope {

          val result = getEntityFromCursor(momentEntityFromCursor)(mockCursor)
          result must beNone
        }

      "return a Moment object when a cursor with data is given" in
        new MomentMockCursor
          with Scope {

          val result = getEntityFromCursor(momentEntityFromCursor)(mockCursor)

          result must beSome[MomentEntity].which { moment =>
            moment.id shouldEqual momentEntity.id
            moment.data shouldEqual momentEntity.data
          }
        }
    }

    "getListFromCursor" should {

      "return an empty sequence when an empty cursor is given" in
        new EmptyMomentMockCursor
          with Scope {

          val result = getListFromCursor(momentEntityFromCursor)(mockCursor)
          result should beEmpty
        }

      "return a Moment sequence when a cursor with data is given" in
        new MomentMockCursor
          with Scope {

          val result = getListFromCursor(momentEntityFromCursor)(mockCursor)
          result shouldEqual momentEntitySeq
        }
    }
  }
}
