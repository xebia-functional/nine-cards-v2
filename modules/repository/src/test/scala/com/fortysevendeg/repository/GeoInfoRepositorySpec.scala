package com.fortysevendeg.repository

import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.commons.{ContentResolverWrapperImpl, GeoInfoUri}
import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategory, GeoInfo, GeoInfoData}
import com.fortysevendeg.ninecardslauncher.repository.provider.GeoInfoEntity.geoInfoEntityFromCursor
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import org.mockito.Mockito._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Random

trait GeoInfoMockCursor extends MockCursor with GeoInfoTestData {

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

trait EmptyGeoInfoMockCursor extends MockCursor with GeoInfoTestData {

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

trait GeoInfoTestData {
  val geoInfoId = Random.nextInt(10)
  val nonExistingGeoInfoId = 15
  val constrain = Random.nextString(5)
  val nonExistingConstrain = Random.nextString(5)
  val occurrence = Random.nextString(5)
  val wifi = Random.nextString(5)
  val latitude = Random.nextDouble()
  val longitude = Random.nextDouble()
  val system = Random.nextInt(10) < 5

  val geoInfoEntitySeq = createGeoInfoEntitySeq(5)
  val geoInfoEntity = geoInfoEntitySeq.head
  val geoInfoSeq = createGeoInfoSeq(5)
  val geoInfo = geoInfoSeq.head

  def createGeoInfoEntitySeq(num: Int) = (0 until num) map (i => GeoInfoEntity(
    id = geoInfoId + i,
    data = GeoInfoEntityData(
      constrain = constrain,
      occurrence = occurrence,
      wifi = wifi,
      latitude = latitude,
      longitude = longitude,
      system = system)))

  def createGeoInfoSeq(num: Int) = (0 until num) map (i => GeoInfo(
    id = geoInfoId + i,
    data = GeoInfoData(
      constrain = constrain,
      occurrence = occurrence,
      wifi = wifi,
      latitude = latitude,
      longitude = longitude,
      system = system)))

  def createGeoInfoValues = Map[String, Any](
    GeoInfoEntity.constrain -> constrain,
    GeoInfoEntity.occurrence -> occurrence,
    GeoInfoEntity.wifi -> wifi,
    GeoInfoEntity.latitude -> latitude,
    GeoInfoEntity.longitude -> longitude,
    GeoInfoEntity.system -> system)
}

trait GeoInfoTestSupport
  extends BaseTestSupport
  with GeoInfoTestData
  with DBUtils
  with Mockito {

  lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]
  lazy val geoInfoRepository = new GeoInfoRepository(contentResolverWrapper)

  def createGeoInfoData = GeoInfoData(
    constrain = constrain,
    occurrence = occurrence,
    wifi = wifi,
    latitude = latitude,
    longitude = longitude,
    system = system)

  when(contentResolverWrapper.insert(GeoInfoUri, createGeoInfoValues)).thenReturn(geoInfoId)

  when(contentResolverWrapper.deleteById(GeoInfoUri, geoInfoId)).thenReturn(1)

  when(contentResolverWrapper.findById(
    nineCardsUri = GeoInfoUri,
    id = geoInfoId,
    projection = GeoInfoEntity.allFields)(
      f = getEntityFromCursor(geoInfoEntityFromCursor))).thenReturn(Some(geoInfoEntity))

  when(contentResolverWrapper.findById(
    nineCardsUri = GeoInfoUri,
    id = nonExistingGeoInfoId,
    projection = GeoInfoEntity.allFields)(
      f = getEntityFromCursor(geoInfoEntityFromCursor))).thenReturn(None)

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = GeoInfoUri,
    projection = GeoInfoEntity.allFields)(
      f = getListFromCursor(geoInfoEntityFromCursor))).thenReturn(geoInfoEntitySeq)

  when(contentResolverWrapper.fetch(
    nineCardsUri = GeoInfoUri,
    projection = GeoInfoEntity.allFields,
    where = s"${GeoInfoEntity.constrain} = ?",
    whereParams = Seq(constrain))(
      f = getEntityFromCursor(geoInfoEntityFromCursor))).thenReturn(Some(geoInfoEntity))

  when(contentResolverWrapper.fetch(
    nineCardsUri = GeoInfoUri,
    projection = GeoInfoEntity.allFields,
    where = s"${GeoInfoEntity.constrain} = ?",
    whereParams = Seq(nonExistingConstrain))(
      f = getEntityFromCursor(geoInfoEntityFromCursor))).thenReturn(None)

  when(contentResolverWrapper.updateById(GeoInfoUri, geoInfoId, createGeoInfoValues)).thenReturn(1)
}

class GeoInfoRepositorySpec
  extends Specification
  with Mockito
  with DisjunctionMatchers
  with GeoInfoTestSupport {

  "GeoInfoRepositoryClient component" should {

    "addGeoInfo should return a valid GeoInfo object" in {

      val result = runTask(geoInfoRepository.addGeoInfo(createGeoInfoData))

      result must be_\/-[GeoInfo].which {
        geoInfo =>
          geoInfo.id shouldEqual geoInfoId
          geoInfo.data.constrain shouldEqual constrain
      }
    }

    "deleteGeoInfo should return a successful result when a valid geoInfo id is given" in {
      val result = runTask(geoInfoRepository.deleteGeoInfo(geoInfo))

      result must be_\/-[Int].which {
        deleted =>
          deleted shouldEqual 1
      }
    }

    "fetchGeoInfoItems should return all the geoInfo items stored in the database" in {
      val result = runTask(geoInfoRepository.fetchGeoInfoItems)

      result must be_\/-[Seq[GeoInfo]].which {
        geoInfoItems =>
          geoInfoItems shouldEqual geoInfoSeq
      }
    }

    "findGeoInfoById should return a GeoInfo object when a existing id is given" in {
      val result = runTask(geoInfoRepository.findGeoInfoById(geoInfoId))

      result must be_\/-[Option[GeoInfo]].which {
        maybeGeoInfo =>
          maybeGeoInfo must beSome[GeoInfo].which { geoInfo =>
            geoInfo.id shouldEqual geoInfoId
            geoInfo.data.constrain shouldEqual constrain
          }
      }
    }

    "findGeoInfoById should return None when a non-existing id is given" in {
      val result = runTask(geoInfoRepository.findGeoInfoById(nonExistingGeoInfoId))

      result must be_\/-[Option[GeoInfo]].which {
        maybeGeoInfo =>
          maybeGeoInfo must beNone
      }
    }

    "fetchGeoInfoByConstrain should return a GeoInfo object when a existing constrain is given" in {
      val result = runTask(geoInfoRepository.fetchGeoInfoByConstrain(constrain))

      result must be_\/-[Option[GeoInfo]].which {
        maybeGeoInfo =>
          maybeGeoInfo must beSome[GeoInfo].which { geoInfo =>
            geoInfo.id shouldEqual geoInfoId
            geoInfo.data.constrain shouldEqual constrain
          }
      }
    }

    "fetchGeoInfoByConstrain should return None when a non-existing constrain is given" in {
      val result = runTask(geoInfoRepository.fetchGeoInfoByConstrain(nonExistingConstrain))

      result must be_\/-[Option[GeoInfo]].which {
        maybeGeoInfo =>
          maybeGeoInfo must beNone
      }
    }

    "updateGeoInfo should return a successful result when the geoInfo item is updated" in {
      val result = runTask(geoInfoRepository.updateGeoInfo(geoInfo))

      result must be_\/-[Int].which {
        updated =>
          updated shouldEqual 1
      }
    }

    "getEntityFromCursor should return None when an empty cursor is given" in
      new EmptyGeoInfoMockCursor
        with Scope {
        val result = getEntityFromCursor(geoInfoEntityFromCursor)(mockCursor)

        result must beNone
      }

    "getEntityFromCursor should return a GeoInfo object when a cursor with data is given" in
      new GeoInfoMockCursor
        with Scope {
        val result = getEntityFromCursor(geoInfoEntityFromCursor)(mockCursor)

        result must beSome[GeoInfoEntity].which { geoInfo =>
          geoInfo.id shouldEqual geoInfoEntity.id
          geoInfo.data shouldEqual geoInfoEntity.data
        }
      }

    "getListFromCursor should return an empty sequence when an empty cursor is given" in
      new EmptyGeoInfoMockCursor
        with Scope {
        val result = getListFromCursor(geoInfoEntityFromCursor)(mockCursor)

        result shouldEqual Seq.empty
      }

    "getEntityFromCursor should return a GeoInfo sequence when a cursor with data is given" in
      new GeoInfoMockCursor
        with Scope {
        val result = getListFromCursor(geoInfoEntityFromCursor)(mockCursor)

        result shouldEqual geoInfoEntitySeq
      }
  }
}
