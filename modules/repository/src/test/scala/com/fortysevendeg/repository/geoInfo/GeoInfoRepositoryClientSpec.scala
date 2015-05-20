package com.fortysevendeg.repository.geoInfo

import com.fortysevendeg.ninecardslauncher.provider.DBUtils._
import com.fortysevendeg.ninecardslauncher.provider.GeoInfoEntity._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class GeoInfoRepositoryClientSpec
    extends Specification
    with Mockito {

  "GeoInfoRepositoryClient component" should {

    "addGeoInfo should return a valid GeoInfo object" in new AddGeoInfoSupport {

      val response = await(repoAddGeoInfo(createAddGeoInfoRequest))

      response.geoInfo.id shouldEqual geoInfoId
      response.geoInfo.data.constrain shouldEqual constrain
    }

    "deleteGeoInfo should return a successful response when a valid geoInfo id is given" in
        new DeleteGeoInfoSupport {
          val response = await(repoDeleteGeoInfo(createDeleteGeoInfoRequest))

          response.deleted shouldEqual 1
        }

    "fetchGeoInfoItems should return all the geoInfo items stored in the database" in
        new FetchGeoInfoItemsSupport {
          val response = await(repoFetchGeoInfoItems(createFetchGeoInfoItemsRequest))

          response.geoInfoItems shouldEqual geoInfoSeq
        }

    "findGeoInfoById should return a GeoInfo object when a existing id is given" in
        new FindGeoInfoByIdSupport {
          val response = await(repoFindGeoInfoById(createFindGeoInfoByIdRequest(id = geoInfoId)))

          response.geoInfo.get.id shouldEqual geoInfoId
          response.geoInfo.get.data.constrain shouldEqual constrain
        }

    "findGeoInfoById should return None when a non-existing id is given" in new FindGeoInfoByIdSupport {
      val response = await(repoFindGeoInfoById(createFindGeoInfoByIdRequest(id = nonExistingGeoInfoId)))

      response.geoInfo shouldEqual None
    }

    "fetchGeoInfoByConstrain should return a GeoInfo object when a existing constrain is given" in
        new FetchGeoInfoByConstrainSupport {
          val response = await(repoFetchGeoInfoByConstrain(createFetchGeoInfoByConstrainRequest(constrain = constrain)))

          response.geoInfo.get.id shouldEqual geoInfoId
          response.geoInfo.get.data.constrain shouldEqual constrain
        }

    "fetchGeoInfoByConstrain should return None when a non-existing constrain is given" in
        new FetchGeoInfoByConstrainSupport {
          val response = await(repoFetchGeoInfoByConstrain(createFetchGeoInfoByConstrainRequest(constrain = nonExistingConstrain)))

          response.geoInfo shouldEqual None
        }

    "updateGeoInfo should return a successful response when the geoInfo item is updated" in
        new UpdateGeoInfoSupport {
          val response = await(repoUpdateGeoInfo(createUpdateGeoInfoRequest))

          response.updated shouldEqual 1
        }

    "getEntityFromCursor should return None when an empty cursor is given" in
        new EmptyGeoInfoMockCursor {
          val result = getEntityFromCursor(geoInfoEntityFromCursor)(mockCursor)

          result shouldEqual None
        }

    "getEntityFromCursor should return a GeoInfo object when a cursor with data is given" in
        new GeoInfoMockCursor {
          val result = getEntityFromCursor(geoInfoEntityFromCursor)(mockCursor)

          result shouldEqual Some(geoInfoEntity)
        }

    "getListFromCursor should return an empty sequence when an empty cursor is given" in
        new EmptyGeoInfoMockCursor {
          val result = getListFromCursor(geoInfoEntityFromCursor)(mockCursor)

          result shouldEqual Seq.empty
        }

    "getEntityFromCursor should return a GeoInfo sequence when a cursor with data is given" in
        new GeoInfoMockCursor {
          val result = getListFromCursor(geoInfoEntityFromCursor)(mockCursor)

          result shouldEqual geoInfoEntitySeq
        }
  }
}
