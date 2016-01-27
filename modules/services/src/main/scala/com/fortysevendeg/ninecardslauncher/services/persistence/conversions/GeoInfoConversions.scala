package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{GeoInfo => RepositoryGeoInfo, GeoInfoData => RepositoryGeoInfoData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.GeoInfo

trait GeoInfoConversions {

  def toGeoInfoSeq(geoInfo: Seq[RepositoryGeoInfo]): Seq[GeoInfo] = geoInfo map toGeoInfo

  def toGeoInfo(geoInfo: RepositoryGeoInfo): GeoInfo =
    GeoInfo(
      id = geoInfo.id,
      constrain = geoInfo.data.constrain,
      occurrence = geoInfo.data.occurrence,
      wifi = geoInfo.data.wifi,
      latitude = geoInfo.data.latitude,
      longitude = geoInfo.data.longitude,
      system = geoInfo.data.system)

  def toRepositoryGeoInfo(geoInfo: GeoInfo): RepositoryGeoInfo =
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

  def toRepositoryGeoInfo(request: UpdateGeoInfoRequest): RepositoryGeoInfo =
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

  def toRepositoryGeoInfoData(request: AddGeoInfoRequest): RepositoryGeoInfoData =
    RepositoryGeoInfoData(
      constrain = request.constrain,
      occurrence = request.occurrence,
      wifi = request.wifi,
      latitude = request.latitude,
      longitude = request.longitude,
      system = request.system
    )
}
