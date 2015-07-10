package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{GeoInfo => RepoGeoInfo, GeoInfoData => RepoGeoInfoData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.GeoInfo

trait GeoInfoConversions {

  def toGeoInfoSeq(geoInfo: Seq[RepoGeoInfo]) = geoInfo map toGeoInfo

  def toGeoInfo(geoInfo: RepoGeoInfo) =
    GeoInfo(
      id = geoInfo.id,
      constrain = geoInfo.data.constrain,
      occurrence = geoInfo.data.occurrence,
      wifi = geoInfo.data.wifi,
      latitude = geoInfo.data.latitude,
      longitude = geoInfo.data.longitude,
      system = geoInfo.data.system)

  def toRepositoryGeoInfo(geoInfo: GeoInfo) =
    RepoGeoInfo(
      id = geoInfo.id,
      data = RepoGeoInfoData(
        constrain = geoInfo.constrain,
        occurrence = geoInfo.occurrence,
        wifi = geoInfo.wifi,
        latitude = geoInfo.latitude,
        longitude = geoInfo.longitude,
        system = geoInfo.system
      )
    )

  def toRepositoryGeoInfo(request: UpdateGeoInfoRequest) =
    RepoGeoInfo(
      id = request.id,
      data = RepoGeoInfoData(
        constrain = request.constrain,
        occurrence = request.occurrence,
        wifi = request.wifi,
        latitude = request.latitude,
        longitude = request.longitude,
        system = request.system
      )
    )

  def toRepositoryGeoInfoData(request: AddGeoInfoRequest) =
    RepoGeoInfoData(
      constrain = request.constrain,
      occurrence = request.occurrence,
      wifi = request.wifi,
      latitude = request.latitude,
      longitude = request.longitude,
      system = request.system
    )
}
