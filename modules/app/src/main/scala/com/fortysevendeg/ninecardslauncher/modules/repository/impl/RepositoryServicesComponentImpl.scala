package com.fortysevendeg.ninecardslauncher.modules.repository.impl

import android.content.ContentResolver
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.repository._
import com.fortysevendeg.ninecardslauncher.repository.{GetAllCacheCategoryRequest, GetSortedCollectionsRequest, NineCardRepositoryClient}

import scala.concurrent.ExecutionContext

trait RepositoryServicesComponentImpl
  extends RepositoryServicesComponent {

  self : AppContextProvider =>

  lazy val repositoryServices = new RepositoryServicesImpl

  class RepositoryServicesImpl
    extends RepositoryServices
    with Conversions
    with NineCardRepositoryClient {

    override implicit val contentResolver: ContentResolver = appContextProvider.get.getContentResolver

    override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

    override def getCollections: Service[GetCollectionsRequest, GetCollectionsResponse] =
      request =>
        getSortedCollections(GetSortedCollectionsRequest()) map {
          response =>
            GetCollectionsResponse(toCollectionSeq(response.collections))
        }

    override def getCacheCategory: Service[GetCacheCategoryRequest, GetCacheCategoryResponse] =
      request => {
        getAllCacheCategory(GetAllCacheCategoryRequest()) map {
          response =>
            GetCacheCategoryResponse(toCacheCategorySeq(response.cacheCategory))
        }
      }

    override def insertGeoInfo: Service[InsertGeoInfoRequest, InsertGeoInfoResponse] =
      request =>
        addGeoInfo(toAddGeoInfoRequest(request)) map {
          response =>
            InsertGeoInfoResponse(response.geoInfo)
        }
  }

}
