package com.fortysevendeg.ninecardslauncher.modules.repository.geoInfo

import com.fortysevendeg.ninecardslauncher.models.GeoInfo
import com.fortysevendeg.ninecardslauncher.repository.model.{GeoInfo => RepositoryGeoInfo, GeoInfoData => RepositoryGeoInfoData, CacheCategoryData, CacheCategory}
import com.fortysevendeg.ninecardslauncher.repository.{AddGeoInfoRequest => RepositoryAddGeoInfoRequest, DeleteGeoInfoRequest => RepositoryDeleteGeoInfoRequest, FetchGeoInfoByConstrainRequest => RepositoryFetchGeoInfoByConstrainRequest, FindGeoInfoByIdRequest => RepositoryFindGeoInfoByIdRequest, UpdateGeoInfoRequest => RepositoryUpdateGeoInfoRequest}

trait Conversions {

  def toGeoInfoSeq(geoInfo: Seq[RepositoryGeoInfo]) = geoInfo map toGeoInfo

  def toGeoInfo(geoInfo: RepositoryGeoInfo) =
    GeoInfo(
      id = geoInfo.id,
      constrain = geoInfo.data.constrain,
      occurrence = geoInfo.data.occurrence,
      wifi = geoInfo.data.wifi,
      latitude = geoInfo.data.latitude,
      longitude = geoInfo.data.longitude,
      system = geoInfo.data.system)

  def toRepositoryGeoInfo(geoInfo: GeoInfo) =
    RepositoryGeoInfo(
      id = geoInfo.id,
      data = RepositoryGeoInfoData(
        constrain = geoInfo.constrain,
        occurrence = geoInfo.occurrence,
        wifi = geoInfo.wifi,
        latitude = geoInfo.latitude,
        longitude = geoInfo.longitude,
        system = geoInfo.system
      )
    )

  def toRepositoryAddGeoInfoRequest(request: AddGeoInfoRequest) =
    RepositoryAddGeoInfoRequest(
      data = RepositoryGeoInfoData(
        constrain = request.constrain,
        occurrence = request.occurrence,
        wifi = request.wifi,
        latitude = request.latitude,
        longitude = request.longitude,
        system = request.system
      )
    )

  def toRepositoryDeleteGeoInfoRequest(request: DeleteGeoInfoRequest) =
    RepositoryDeleteGeoInfoRequest(geoInfo = toRepositoryGeoInfo(request.geoInfo))

  def toRepositoryFetchGeoInfoByConstrainRequest(request: FetchGeoInfoByConstrainRequest) =
    RepositoryFetchGeoInfoByConstrainRequest(constrain = request.constrain)

  def toRepositoryFindGeoInfoByIdRequest(request: FindGeoInfoByIdRequest) =
    RepositoryFindGeoInfoByIdRequest(id = request.id)

  def toRepositoryUpdateGeoInfoRequest(request: UpdateGeoInfoRequest) =
    RepositoryUpdateGeoInfoRequest(
      RepositoryGeoInfo(
        id = request.id,
        data = RepositoryGeoInfoData(
          constrain = request.constrain,
          occurrence = request.occurrence,
          wifi = request.wifi,
          latitude = request.latitude,
          longitude = request.longitude,
          system = request.system
        )
      )
    )
}
