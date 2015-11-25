package com.fortysevendeg.repository.geoinfo

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.GeoInfo
import com.fortysevendeg.ninecardslauncher.repository.provider.GeoInfoEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

trait GeoInfoRepositorySpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait GeoInfoRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val geoInfoRepository = new GeoInfoRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]
  }

  trait ValidGeoInfoRepositoryResponses
    extends GeoInfoRepositoryTestData {

    self: GeoInfoRepositoryScope =>

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(mockUri, createGeoInfoValues) returns testGeoInfoId

    contentResolverWrapper.delete(mockUri, where = "") returns 1

    contentResolverWrapper.deleteById(mockUri, testGeoInfoId) returns 1

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testGeoInfoId,
      projection = allFields)(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) returns Some(geoInfoEntity)

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testNonExistingGeoInfoId,
      projection = allFields)(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) returns None

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields)(
        f = getListFromCursor(geoInfoEntityFromCursor)) returns geoInfoEntitySeq

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$constrain = ?",
      whereParams = Seq(testConstrain))(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) returns Some(geoInfoEntity)

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$constrain = ?",
      whereParams = Seq(testNonExistingConstrain))(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) returns None

    contentResolverWrapper.updateById(mockUri, testGeoInfoId, createGeoInfoValues) returns 1
  }

  trait ErrorGeoInfoRepositoryResponses
    extends GeoInfoRepositoryTestData {

    self: GeoInfoRepositoryScope =>

    val contentResolverException = new RuntimeException("Irrelevant message")

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(mockUri, createGeoInfoValues) throws contentResolverException

    contentResolverWrapper.delete(mockUri, where = "") throws contentResolverException

    contentResolverWrapper.deleteById(mockUri, testGeoInfoId) throws contentResolverException

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testGeoInfoId,
      projection = allFields)(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields)(
        f = getListFromCursor(geoInfoEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$constrain = ?",
      whereParams = Seq(testConstrain))(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(mockUri, testGeoInfoId, createGeoInfoValues) throws contentResolverException
  }

}

trait GeoInfoMockCursor
  extends MockCursor
  with GeoInfoRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, geoInfoSeq map (_.id), IntDataType),
    (constrain, 1, geoInfoSeq map (_.data.constrain), StringDataType),
    (occurrence, 2, geoInfoSeq map (_.data.occurrence), StringDataType),
    (wifi, 3, geoInfoSeq map (_.data.wifi), StringDataType),
    (latitude, 4, geoInfoSeq map (_.data.latitude), DoubleDataType),
    (longitude, 5, geoInfoSeq map (_.data.longitude), DoubleDataType),
    (system, 6, geoInfoSeq map (item => if (item.data.system) 1 else 0), IntDataType)
  )

  prepareCursor[GeoInfo](geoInfoSeq.size, cursorData)
}

trait EmptyGeoInfoMockCursor
  extends MockCursor
  with GeoInfoRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (constrain, 1, Seq.empty, StringDataType),
    (occurrence, 2, Seq.empty, StringDataType),
    (wifi, 3, Seq.empty, StringDataType),
    (latitude, 4, Seq.empty, DoubleDataType),
    (longitude, 5, Seq.empty, DoubleDataType),
    (system, 6, Seq.empty, IntDataType)
  )

  prepareCursor[GeoInfo](0, cursorData)
}

class GeoInfoRepositorySpec
  extends GeoInfoRepositorySpecification {

  "GeoInfoRepositoryClient component" should {

    "addGeoInfo" should {
      "return a GeoInfo object with a valid request" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.addGeoInfo(createGeoInfoData).run.run

          result must beLike {
            case Answer(geoInfo) =>
              geoInfo.id shouldEqual testGeoInfoId
              geoInfo.data.constrain shouldEqual testConstrain
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.addGeoInfo(createGeoInfoData).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteGeoInfos" should {

      "return a successful result when all the geoInfos are deleted" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.deleteGeoInfoItems().run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.deleteGeoInfoItems().run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteGeoInfo" should {

      "return a successful result when a valid geoInfo id is given" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.deleteGeoInfo(geoInfo).run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.deleteGeoInfo(geoInfo).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "fetchGeoInfoItems" should {

      "return all the geoInfo items stored in the database" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.fetchGeoInfoItems.run.run

          result must beLike {
            case Answer(geoInfoItems) =>
              geoInfoItems shouldEqual geoInfoSeq
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.fetchGeoInfoItems.run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "findGeoInfoById" should {

      "return a GeoInfo object when a existing id is given" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.findGeoInfoById(testGeoInfoId).run.run

          result must beLike {
            case Answer(maybeGeoInfo) =>
              maybeGeoInfo must beSome[GeoInfo].which { geoInfo =>
                geoInfo.id shouldEqual testGeoInfoId
                geoInfo.data.constrain shouldEqual testConstrain
              }
          }
        }

      "return None when a non-existent id is given" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.findGeoInfoById(testNonExistingGeoInfoId).run.run

          result must beLike {
            case Answer(maybeGeoInfo) =>
              maybeGeoInfo must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.findGeoInfoById(testGeoInfoId).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "fetchGeoInfoByConstrain" should {

      "return a GeoInfo object when a existing constrain is given" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.fetchGeoInfoByConstrain(testConstrain).run.run

          result must beLike {
            case Answer(maybeGeoInfo) =>
              maybeGeoInfo must beSome[GeoInfo].which { geoInfo =>
                geoInfo.id shouldEqual testGeoInfoId
                geoInfo.data.constrain shouldEqual testConstrain
              }
          }
        }

      "return None when a non-existent constrain is given" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.fetchGeoInfoByConstrain(testNonExistingConstrain).run.run

          result must beLike {
            case Answer(maybeGeoInfo) =>
              maybeGeoInfo must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.fetchGeoInfoByConstrain(testConstrain).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "updateGeoInfo" should {

      "return a successful result when the geoInfo item is updated" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.updateGeoInfo(geoInfo).run.run

          result must beLike {
            case Answer(updated) =>
              updated shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.updateGeoInfo(geoInfo).run.run

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
        new EmptyGeoInfoMockCursor
          with GeoInfoRepositoryScope {

          val result = getEntityFromCursor(geoInfoEntityFromCursor)(mockCursor)

          result must beNone
        }

      "return a GeoInfo object when a cursor with data is given" in
        new GeoInfoMockCursor
          with GeoInfoRepositoryScope {

          val result = getEntityFromCursor(geoInfoEntityFromCursor)(mockCursor)

          result must beSome[GeoInfoEntity].which { geoInfo =>
            geoInfo.id shouldEqual geoInfoEntity.id
            geoInfo.data shouldEqual geoInfoEntity.data
          }
        }
    }

    "getListFromCursor" should {

      "return an empty sequence when an empty cursor is given" in
        new EmptyGeoInfoMockCursor
          with GeoInfoRepositoryScope {

          val result = getListFromCursor(geoInfoEntityFromCursor)(mockCursor)

          result should beEmpty
        }

      "return a GeoInfo sequence when a cursor with data is given" in
        new GeoInfoMockCursor
          with GeoInfoRepositoryScope {

          val result = getListFromCursor(geoInfoEntityFromCursor)(mockCursor)

          result shouldEqual geoInfoEntitySeq
        }
    }
  }
}
