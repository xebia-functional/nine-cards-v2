package com.fortysevendeg.repository.geoInfo

import com.fortysevendeg.ninecardslauncher.commons.GeoInfoUri
import com.fortysevendeg.ninecardslauncher.provider.DBUtils._
import com.fortysevendeg.ninecardslauncher.provider.GeoInfoEntity._
import com.fortysevendeg.ninecardslauncher.provider._
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.{GeoInfo, GeoInfoData}
import com.fortysevendeg.ninecardslauncher.repository.repositories.GeoInfoRepositoryClient
import com.fortysevendeg.repository._
import org.mockito.Mockito._
import org.specs2.specification.Scope

import scala.util.Random

trait GeoInfoTestSupport
    extends BaseTestSupport
    with GeoInfoRepositoryClient
    with GeoInfoTestData
    with MockContentResolverWrapper
    with Scope

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

trait GeoInfoMockCursor extends MockCursor with GeoInfoTestData with Scope {

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

trait EmptyGeoInfoMockCursor extends MockCursor with GeoInfoTestData with Scope {

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

trait AddGeoInfoSupport extends GeoInfoTestSupport {

  def createAddGeoInfoRequest = AddGeoInfoRequest(GeoInfoData(
    constrain = constrain,
    occurrence = occurrence,
    wifi = wifi,
    latitude = latitude,
    longitude = longitude,
    system = system))

  when(contentResolverWrapper.insert(GeoInfoUri, createGeoInfoValues)).thenReturn(geoInfoId)
}

trait DeleteGeoInfoSupport extends GeoInfoTestSupport {

  def createDeleteGeoInfoRequest = DeleteGeoInfoRequest(geoInfo = geoInfo)

  when(contentResolverWrapper.deleteById(GeoInfoUri, geoInfoId)).thenReturn(1)
}

trait FindGeoInfoByIdSupport extends GeoInfoTestSupport {

  def createFindGeoInfoByIdRequest(id: Int) = FindGeoInfoByIdRequest(id = id)

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
}

trait FetchGeoInfoByConstrainSupport extends GeoInfoTestSupport {

  def createFetchGeoInfoByConstrainRequest(constrain: String) = FetchGeoInfoByConstrainRequest(constrain = constrain)

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
}

trait FetchGeoInfoItemsSupport extends GeoInfoTestSupport {

  def createFetchGeoInfoItemsRequest = FetchGeoInfoItemsRequest()

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = GeoInfoUri,
    projection = AllFields)(
        f = getListFromCursor(geoInfoEntityFromCursor))).thenReturn(geoInfoEntitySeq)
}

trait UpdateGeoInfoSupport extends GeoInfoTestSupport {

  def createUpdateGeoInfoRequest = UpdateGeoInfoRequest(geoInfo = geoInfo)

  when(contentResolverWrapper.updateById(GeoInfoUri, geoInfoId, createGeoInfoValues)).thenReturn(1)
}
