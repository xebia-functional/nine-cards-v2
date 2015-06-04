package com.fortysevendeg.repository

import com.fortysevendeg.ninecardslauncher.commons.GeoInfoUri
import com.fortysevendeg.ninecardslauncher.provider.GeoInfoEntity._
import com.fortysevendeg.ninecardslauncher.provider._
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.{GeoInfoData, GeoInfo}
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Random

trait GeoInfoMockCursor extends MockCursor with GeoInfoTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.Id, 0, geoInfoSeq map (_.id), IntDataType),
    (Constrain, 1, geoInfoSeq map (_.data.constrain), StringDataType),
    (Occurrence, 2, geoInfoSeq map (_.data.occurrence), StringDataType),
    (Wifi, 3, geoInfoSeq map (_.data.wifi), StringDataType),
    (Latitude, 4, geoInfoSeq map (_.data.latitude), DoubleDataType),
    (Longitude, 5, geoInfoSeq map (_.data.longitude), DoubleDataType),
    (System, 6, geoInfoSeq map (item => if (item.data.system) 1 else 0), IntDataType)
  )

  prepareCursor[GeoInfo](geoInfoSeq.size, cursorData)
}

trait EmptyGeoInfoMockCursor extends MockCursor with GeoInfoTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.Id, 0, Seq.empty, IntDataType),
    (Constrain, 1, Seq.empty, StringDataType),
    (Occurrence, 2, Seq.empty, StringDataType),
    (Wifi, 3, Seq.empty, StringDataType),
    (Latitude, 4, Seq.empty, DoubleDataType),
    (Longitude, 5, Seq.empty, DoubleDataType),
    (System, 6, Seq.empty, IntDataType)
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
    Constrain -> constrain,
    Occurrence -> occurrence,
    Wifi -> wifi,
    Latitude -> latitude,
    Longitude -> longitude,
    System -> system)
}

trait GeoInfoTestSupport
    extends BaseTestSupport
    with MockContentResolverWrapper
    with GeoInfoTestData
    with DBUtils {

  lazy val client = new GeoInfoRepositoryClient(contentResolverWrapper)

  def createAddGeoInfoRequest = AddGeoInfoRequest(GeoInfoData(
    constrain = constrain,
    occurrence = occurrence,
    wifi = wifi,
    latitude = latitude,
    longitude = longitude,
    system = system))

  def createDeleteGeoInfoRequest = DeleteGeoInfoRequest(geoInfo = geoInfo)

  def createGetAllGeoInfoItemsRequest = GetAllGeoInfoItemsRequest()

  def createGetGeoInfoByIdRequest(id: Int) = GetGeoInfoByIdRequest(id = id)

  def createGetGeoInfoByConstrainRequest(constrain: String) = GetGeoInfoByConstrainRequest(constrain = constrain)

  def createUpdateGeoInfoRequest = UpdateGeoInfoRequest(geoInfo = geoInfo)

  when(contentResolverWrapper.insert(GeoInfoUri, createGeoInfoValues)).thenReturn(geoInfoId)

  when(contentResolverWrapper.deleteById(GeoInfoUri, geoInfoId)).thenReturn(1)

  when(contentResolverWrapper.findById(
    nineCardsUri = GeoInfoUri,
    id = geoInfoId,
    projection = AllFields)(
        f = getEntityFromCursor(geoInfoEntityFromCursor))).thenReturn(Some(geoInfoEntity))

  when(contentResolverWrapper.findById(
    nineCardsUri = GeoInfoUri,
    id = nonExistingGeoInfoId,
    projection = AllFields)(
        f = getEntityFromCursor(geoInfoEntityFromCursor))).thenReturn(None)

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = GeoInfoUri,
    projection = AllFields)(
        f = getListFromCursor(geoInfoEntityFromCursor))).thenReturn(geoInfoEntitySeq)

  when(contentResolverWrapper.fetch(
    nineCardsUri = GeoInfoUri,
    projection = AllFields,
    where = s"$Constrain = ?",
    whereParams = Seq(constrain))(
        f = getEntityFromCursor(geoInfoEntityFromCursor))).thenReturn(Some(geoInfoEntity))

  when(contentResolverWrapper.fetch(
    nineCardsUri = GeoInfoUri,
    projection = AllFields,
    where = s"$Constrain = ?",
    whereParams = Seq(nonExistingConstrain))(
        f = getEntityFromCursor(geoInfoEntityFromCursor))).thenReturn(None)

  when(contentResolverWrapper.updateById(GeoInfoUri, geoInfoId, createGeoInfoValues)).thenReturn(1)
}

class GeoInfoRepositoryClientSpec
    extends Specification
    with Mockito
    with GeoInfoTestSupport {

  "GeoInfoRepositoryClient component" should {

    "addGeoInfo should return a valid GeoInfo object" in {

      val response = await(client.addGeoInfo(createAddGeoInfoRequest))

      response.geoInfo.get.id shouldEqual geoInfoId
      response.geoInfo.get.data.constrain shouldEqual constrain
    }

    "deleteGeoInfo should return a successful response when a valid geoInfo id is given" in {
      val response = await(client.deleteGeoInfo(createDeleteGeoInfoRequest))

      response.deleted shouldEqual 1
    }

    "getAllGeoInfoItems should return all the geoInfo items stored in the database" in {
      val response = await(client.getAllGeoInfoItems(createGetAllGeoInfoItemsRequest))

      response.geoInfoItems shouldEqual geoInfoSeq
    }

    "getGeoInfoById should return a GeoInfo object when a existing id is given" in {
      val response = await(client.getGeoInfoById(createGetGeoInfoByIdRequest(id = geoInfoId)))

      response.result.get.id shouldEqual geoInfoId
      response.result.get.data.constrain shouldEqual constrain
    }

    "getGeoInfoById should return None when a non-existing id is given" in {
      val response = await(client.getGeoInfoById(createGetGeoInfoByIdRequest(id = nonExistingGeoInfoId)))

      response.result shouldEqual None
    }

    "getGeoInfoByConstrain should return a GeoInfo object when a existing constrain is given" in {
      val response = await(client.getGeoInfoByConstrain(createGetGeoInfoByConstrainRequest(constrain = constrain)))

      response.result.get.id shouldEqual geoInfoId
      response.result.get.data.constrain shouldEqual constrain
    }

    "getGeoInfoByConstrain should return None when a non-existing constrain is given" in {
      val response = await(client.getGeoInfoByConstrain(createGetGeoInfoByConstrainRequest(constrain = nonExistingConstrain)))

      response.result shouldEqual None
    }

    "updateGeoInfo should return a successful response when the geoInfo item is updated" in {
      val response = await(client.updateGeoInfo(createUpdateGeoInfoRequest))

      response.updated shouldEqual 1
    }

    "getEntityFromCursor should return None when an empty cursor is given" in
        new EmptyGeoInfoMockCursor
            with Scope {
          val result = getEntityFromCursor(geoInfoEntityFromCursor)(mockCursor)

          result shouldEqual None
        }

    "getEntityFromCursor should return a GeoInfo object when a cursor with data is given" in
        new GeoInfoMockCursor
            with Scope {
          val result = getEntityFromCursor(geoInfoEntityFromCursor)(mockCursor)

          result shouldEqual Some(geoInfoEntity)
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
