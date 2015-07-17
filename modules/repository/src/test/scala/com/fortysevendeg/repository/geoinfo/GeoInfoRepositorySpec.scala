package com.fortysevendeg.repository.geoinfo

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.repository.commons.{ContentResolverWrapperImpl, GeoInfoUri}
import com.fortysevendeg.ninecardslauncher.repository.model.GeoInfo
import com.fortysevendeg.ninecardslauncher.repository.provider.GeoInfoEntity.geoInfoEntityFromCursor
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait GeoInfoRepositorySpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait GeoInfoRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]
    lazy val geoInfoRepository = new GeoInfoRepository(contentResolverWrapper)
  }

  trait ValidGeoInfoRepositoryResponses
    extends DBUtils
    with GeoInfoRepositoryTestData {

    self: GeoInfoRepositoryScope =>

    contentResolverWrapper.insert(GeoInfoUri, createGeoInfoValues) returns geoInfoId

    contentResolverWrapper.deleteById(GeoInfoUri, geoInfoId) returns 1

    contentResolverWrapper.findById(
      nineCardsUri = GeoInfoUri,
      id = geoInfoId,
      projection = GeoInfoEntity.allFields)(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) returns Some(geoInfoEntity)

    contentResolverWrapper.findById(
      nineCardsUri = GeoInfoUri,
      id = nonExistingGeoInfoId,
      projection = GeoInfoEntity.allFields)(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) returns None

    contentResolverWrapper.fetchAll(
      nineCardsUri = GeoInfoUri,
      projection = GeoInfoEntity.allFields)(
        f = getListFromCursor(geoInfoEntityFromCursor)) returns geoInfoEntitySeq

    contentResolverWrapper.fetch(
      nineCardsUri = GeoInfoUri,
      projection = GeoInfoEntity.allFields,
      where = s"${GeoInfoEntity.constrain} = ?",
      whereParams = Seq(constrain))(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) returns Some(geoInfoEntity)

    contentResolverWrapper.fetch(
      nineCardsUri = GeoInfoUri,
      projection = GeoInfoEntity.allFields,
      where = s"${GeoInfoEntity.constrain} = ?",
      whereParams = Seq(nonExistingConstrain))(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) returns None

    contentResolverWrapper.updateById(GeoInfoUri, geoInfoId, createGeoInfoValues) returns 1
  }

  trait ErrorGeoInfoRepositoryResponses
    extends DBUtils
    with GeoInfoRepositoryTestData {

    self: GeoInfoRepositoryScope =>

    val contentResolverException = new RuntimeException("Irrelevant message")

    contentResolverWrapper.insert(GeoInfoUri, createGeoInfoValues) throws contentResolverException

    contentResolverWrapper.deleteById(GeoInfoUri, geoInfoId) throws contentResolverException

    contentResolverWrapper.findById(
      nineCardsUri = GeoInfoUri,
      id = geoInfoId,
      projection = GeoInfoEntity.allFields)(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      nineCardsUri = GeoInfoUri,
      projection = GeoInfoEntity.allFields)(
        f = getListFromCursor(geoInfoEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      nineCardsUri = GeoInfoUri,
      projection = GeoInfoEntity.allFields,
      where = s"${GeoInfoEntity.constrain} = ?",
      whereParams = Seq(constrain))(
        f = getEntityFromCursor(geoInfoEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(GeoInfoUri, geoInfoId, createGeoInfoValues) throws contentResolverException
  }

}

trait GeoInfoMockCursor
  extends MockCursor
  with DBUtils
  with GeoInfoRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, geoInfoSeq map (_.id), IntDataType),
    (GeoInfoEntity.constrain, 1, geoInfoSeq map (_.data.constrain), StringDataType),
    (GeoInfoEntity.occurrence, 2, geoInfoSeq map (_.data.occurrence), StringDataType),
    (GeoInfoEntity.wifi, 3, geoInfoSeq map (_.data.wifi), StringDataType),
    (GeoInfoEntity.latitude, 4, geoInfoSeq map (_.data.latitude), DoubleDataType),
    (GeoInfoEntity.longitude, 5, geoInfoSeq map (_.data.longitude), DoubleDataType),
    (GeoInfoEntity.system, 6, geoInfoSeq map (item => if (item.data.system) 1 else 0), IntDataType)
  )

  prepareCursor[GeoInfo](geoInfoSeq.size, cursorData)
}

trait EmptyGeoInfoMockCursor
  extends MockCursor
  with DBUtils
  with GeoInfoRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (GeoInfoEntity.constrain, 1, Seq.empty, StringDataType),
    (GeoInfoEntity.occurrence, 2, Seq.empty, StringDataType),
    (GeoInfoEntity.wifi, 3, Seq.empty, StringDataType),
    (GeoInfoEntity.latitude, 4, Seq.empty, DoubleDataType),
    (GeoInfoEntity.longitude, 5, Seq.empty, DoubleDataType),
    (GeoInfoEntity.system, 6, Seq.empty, IntDataType)
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

          val result = geoInfoRepository.addGeoInfo(createGeoInfoData).run

          result must be_\/-[GeoInfo].which {
            geoInfo =>
              geoInfo.id shouldEqual geoInfoId
              geoInfo.data.constrain shouldEqual constrain
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.addGeoInfo(createGeoInfoData).run

          result must be_-\/[NineCardsException]
        }
    }

    "deleteGeoInfo" should {

      "return a successful result when a valid geoInfo id is given" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.deleteGeoInfo(geoInfo).run

          result must be_\/-[Int].which {
            deleted =>
              deleted shouldEqual 1
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.deleteGeoInfo(geoInfo).run

          result must be_-\/[NineCardsException]
        }
    }

    "fetchGeoInfoItems" should {

      "return all the geoInfo items stored in the database" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.fetchGeoInfoItems.run

          result must be_\/-[Seq[GeoInfo]].which {
            geoInfoItems =>
              geoInfoItems shouldEqual geoInfoSeq
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.fetchGeoInfoItems.run

          result must be_-\/[NineCardsException]
        }
    }

    "findGeoInfoById" should {

      "return a GeoInfo object when a existing id is given" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.findGeoInfoById(geoInfoId).run

          result must be_\/-[Option[GeoInfo]].which {
            maybeGeoInfo =>
              maybeGeoInfo must beSome[GeoInfo].which { geoInfo =>
                geoInfo.id shouldEqual geoInfoId
                geoInfo.data.constrain shouldEqual constrain
              }
          }
        }

      "return None when a non-existent id is given" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.findGeoInfoById(nonExistingGeoInfoId).run

          result must be_\/-[Option[GeoInfo]].which {
            maybeGeoInfo =>
              maybeGeoInfo must beNone
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.findGeoInfoById(geoInfoId).run

          result must be_-\/[NineCardsException]
        }
    }

    "fetchGeoInfoByConstrain" should {

      "return a GeoInfo object when a existing constrain is given" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.fetchGeoInfoByConstrain(constrain).run

          result must be_\/-[Option[GeoInfo]].which {
            maybeGeoInfo =>
              maybeGeoInfo must beSome[GeoInfo].which { geoInfo =>
                geoInfo.id shouldEqual geoInfoId
                geoInfo.data.constrain shouldEqual constrain
              }
          }
        }

      "return None when a non-existent constrain is given" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.fetchGeoInfoByConstrain(nonExistingConstrain).run

          result must be_\/-[Option[GeoInfo]].which {
            maybeGeoInfo =>
              maybeGeoInfo must beNone
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.fetchGeoInfoByConstrain(constrain).run

          result must be_-\/[NineCardsException]
        }
    }

    "updateGeoInfo" should {

      "return a successful result when the geoInfo item is updated" in
        new GeoInfoRepositoryScope
          with ValidGeoInfoRepositoryResponses {

          val result = geoInfoRepository.updateGeoInfo(geoInfo).run

          result must be_\/-[Int].which {
            updated =>
              updated shouldEqual 1
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new GeoInfoRepositoryScope
          with ErrorGeoInfoRepositoryResponses {

          val result = geoInfoRepository.updateGeoInfo(geoInfo).run

          result must be_-\/[NineCardsException]
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

          result shouldEqual Seq.empty
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
