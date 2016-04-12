package com.fortysevendeg.repository.moment

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.Moment
import com.fortysevendeg.ninecardslauncher.repository.provider.MomentEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories.MomentRepository
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

trait MomentRepositorySpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait MomentRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val momentRepository = new MomentRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]
  }

  trait ValidMomentRepositoryResponses
    extends MomentRepositoryTestData {

    self: MomentRepositoryScope =>

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createMomentValues,
      notificationUri = Some(mockUri)) returns testId

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testId,
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testId,
      projection = allFields)(
        f = getEntityFromCursor(momentEntityFromCursor)) returns Some(momentEntity)

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testNonExistingId,
      projection = allFields)(
        f = getEntityFromCursor(momentEntityFromCursor)) returns None

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = testId,
      values = createMomentValues,
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = "",
      whereParams = Seq.empty,
      orderBy = "")(
      f = getListFromCursor(momentEntityFromCursor)) returns momentEntitySeq

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$wifi = ?",
      whereParams = Seq(testWifi.toString),
      orderBy = "")(
      f = getListFromCursor(momentEntityFromCursor)) returns momentEntitySeq

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$wifi = ?",
      whereParams = Seq(testWifi.toString))(
      f = getListFromCursor(momentEntityFromCursor)) returns momentEntitySeq
  }

  trait ValidMomentRepositoryCollectionResponses
    extends MomentRepositoryTestData {

    self: MomentRepositoryScope =>

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createMomentValuesCollection,
      notificationUri = Some(mockUri)) returns testId
  }

  trait ErrorMomentRepositoryResponses
    extends MomentRepositoryTestData {

    self: MomentRepositoryScope =>

    val contentResolverException = new RuntimeException("Irrelevant message")

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createMomentValues,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testId,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testId,
      projection = allFields)(
        f = getEntityFromCursor(momentEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = testId,
      values = createMomentValues,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = "",
      whereParams = Seq.empty,
      orderBy = "")(
      f = getListFromCursor(momentEntityFromCursor)) throws contentResolverException
  }

}

trait MomentMockCursor
  extends MockCursor
  with MomentRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, momentSeq map (_.id), IntDataType),
    (collectionId, 1, momentSeq map (_.data.collectionId getOrElse(javaNull)), IntDataType),
    (timeslot, 2, momentSeq map (_.data.timeslot), StringDataType),
    (wifi, 4, momentSeq map (_.data.wifi), StringDataType),
    (headphone, 5, momentSeq map (item => if (item.data.headphone) 1 else 0), IntDataType))

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
    (headphone, 5, Seq.empty, IntDataType))

  prepareCursor[Moment](0, cursorData)
}

class MomentRepositorySpec
  extends MomentRepositorySpecification {

  "MomentRepositoryClient component" should {

    "addMoment" should {

      "return a Moment object with a valid request" in
        new MomentRepositoryScope
          with ValidMomentRepositoryResponses {

          val result = momentRepository.addMoment(data = createMomentData).run.run

          result must beLike {
            case Answer(momentResult) =>
              momentResult.id shouldEqual testId
              momentResult.data.collectionId shouldEqual testCollectionIdOption
          }
        }

      "return a Moment object with a collectionId = None with a valid request with a collectionId = None" in
        new MomentRepositoryScope
          with ValidMomentRepositoryCollectionResponses {

          val result = momentRepository.addMoment(data = createMomentDataCollection).run.run

          result must beLike {
            case Answer(momentResult) =>
              momentResult.id shouldEqual testId
              momentResult.data.collectionId shouldEqual None
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope
          with ErrorMomentRepositoryResponses {

          val result = momentRepository.addMoment(data = createMomentData).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteMoments" should {

      "return a successful result when all the moments are deleted" in
        new MomentRepositoryScope
          with ValidMomentRepositoryResponses {

          val result = momentRepository.deleteMoments().run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope
          with ErrorMomentRepositoryResponses {

          val result = momentRepository.deleteMoments().run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteMoment" should {

      "return a successful result when a valid moment id is given" in
        new MomentRepositoryScope
          with ValidMomentRepositoryResponses {

          val result = momentRepository.deleteMoment(moment).run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope
          with ErrorMomentRepositoryResponses {

          val result = momentRepository.deleteMoment(moment).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "findMomentById" should {

      "return a Moment object when a existing id is given" in
        new MomentRepositoryScope
          with ValidMomentRepositoryResponses {

          val result = momentRepository.findMomentById(id = testId).run.run

          result must beLike {
            case Answer(maybeMoment) =>
              maybeMoment must beSome[Moment].which { moment =>
                moment.id shouldEqual testId
                moment.data.collectionId shouldEqual testCollectionIdOption
              }
          }
        }

      "return None when a non-existing id is given" in
        new MomentRepositoryScope
          with ValidMomentRepositoryResponses {
          val result = momentRepository.findMomentById(id = testNonExistingId).run.run

          result must beLike {
            case Answer(maybeMoment) =>
              maybeMoment must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope
          with ErrorMomentRepositoryResponses {

          val result = momentRepository.findMomentById(id = testId).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "updateMoment" should {

      "return a successful result when the moment is updated" in
        new MomentRepositoryScope
          with ValidMomentRepositoryResponses {

          val result = momentRepository.updateMoment(item = moment).run.run

          result must beLike {
            case Answer(updated) =>
              updated shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope
          with ErrorMomentRepositoryResponses {

          val result = momentRepository.updateMoment(item = moment).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "fetchMoments" should {

      "return all Moments" in
        new MomentRepositoryScope
          with ValidMomentRepositoryResponses {

          val result = momentRepository.fetchMoments().run.run

          result must beLike {
            case Answer(moments) =>
              moments shouldEqual momentSeq
          }
        }

      "return all Moments that match the where clause" in
        new MomentRepositoryScope
          with ValidMomentRepositoryResponses {

          val result = momentRepository.fetchMoments(where = s"$wifi = ?", whereParams = Seq(testWifi.toString)).run.run

          result must beLike {
            case Answer(moments) =>
              moments shouldEqual momentSeq
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new MomentRepositoryScope
          with ErrorMomentRepositoryResponses {

          val result = momentRepository.fetchMoments().run.run

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
