package com.fortysevendeg.ninecardslauncher.modules.repository

import com.fortysevendeg.ninecardslauncher.commons.Service

trait RepositoryServices {
  def getCollections: Service[GetCollectionsRequest, GetCollectionsResponse]
  def getCacheCategory: Service[GetCacheCategoryRequest, GetCacheCategoryResponse]
  def insertGeoInfo: Service[InsertGeoInfoRequest, InsertGeoInfoResponse]
  def insertCollection: Service[InsertCollectionRequest, InsertCollectionResponse]
}

trait RepositoryServicesComponent {
  val repositoryServices: RepositoryServices
}
