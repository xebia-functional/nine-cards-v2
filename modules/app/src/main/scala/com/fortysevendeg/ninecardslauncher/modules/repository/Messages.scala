package com.fortysevendeg.ninecardslauncher.modules.repository

import com.fortysevendeg.ninecardslauncher.repository.model.GeoInfo

case class GetCollectionsRequest()

case class GetCollectionsResponse(collections: Seq[Collection])

case class GetCacheCategoryRequest()

case class GetCacheCategoryResponse(cacheCategory: Seq[CacheCategory])

case class InsertGeoInfoRequest(
  constrain: String,
  occurrence: String,
  wifi: String,
  latitude: Double,
  longitude: Double,
  system: Boolean)

case class InsertGeoInfoResponse(
  geoInfo: Option[GeoInfo])
