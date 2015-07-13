package com.fortysevendeg.ninecardslauncher.modules.repository.geoInfo

import com.fortysevendeg.ninecardslauncher.models.GeoInfo

case class AddGeoInfoRequest(
    constrain: String,
    occurrence: String,
    wifi: String,
    latitude: Double,
    longitude: Double,
    system: Boolean)

case class AddGeoInfoResponse(
    geoInfo: GeoInfo)

case class DeleteGeoInfoRequest(geoInfo: GeoInfo)

case class DeleteGeoInfoResponse(deleted: Int)

case class FetchGeoInfoItemsRequest()

case class FetchGeoInfoItemsResponse(geoInfoItems: Seq[GeoInfo])

case class FindGeoInfoByIdRequest(id: Int)

case class FindGeoInfoByIdResponse(geoInfo: Option[GeoInfo])

case class FetchGeoInfoByConstrainRequest(constrain: String)

case class FetchGeoInfoByConstrainResponse(geoInfo: Option[GeoInfo])

case class UpdateGeoInfoRequest(
    id: Int,
    constrain: String,
    occurrence: String,
    wifi: String,
    latitude: Double,
    longitude: Double,
    system: Boolean)

case class UpdateGeoInfoResponse(updated: Int)