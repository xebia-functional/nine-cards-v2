package com.fortysevendeg.ninecardslauncher.modules.repository

import com.fortysevendeg.ninecardslauncher.commons.Service

trait RepositoryServices {

  def getCollections: Service[GetCollectionsRequest, GetCollectionsResponse]

  def insertCacheCategory: Service[InsertCacheCategoryRequest, InsertCacheCategoryResponse]

  def getCacheCategory: Service[GetCacheCategoryRequest, GetCacheCategoryResponse]

  def insertGeoInfo: Service[InsertGeoInfoRequest, InsertGeoInfoResponse]

  def insertCollection: Service[InsertCollectionRequest, InsertCollectionResponse]
}
