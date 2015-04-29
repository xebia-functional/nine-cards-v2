package com.fortysevendeg.ninecardslauncher.modules.repository.impl

import android.content.ContentResolver
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.repository.Conversions
import com.fortysevendeg.ninecardslauncher.modules.repository._
import com.fortysevendeg.ninecardslauncher.repository._

import scala.concurrent.{Future, Promise, ExecutionContext}
import scala.util.Success

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

    override def insertCacheCategory: Service[InsertCacheCategoryRequest, InsertCacheCategoryResponse] =
      request => {
        addCacheCategory(toAddCacheCategoryRequest(request)) map {
          response =>
            InsertCacheCategoryResponse(response.cacheCategory map toCacheCategory)
        }
      }

    override def getCacheCategory: Service[GetCacheCategoryRequest, GetCacheCategoryResponse] =
      request => {
        getAllCacheCategories(GetAllCacheCategoriesRequest()) map {
          response =>
            GetCacheCategoryResponse(toCacheCategorySeq(response.cacheCategories))
        }
      }

    override def insertGeoInfo: Service[InsertGeoInfoRequest, InsertGeoInfoResponse] =
      request =>
        addGeoInfo(toAddGeoInfoRequest(request)) map {
          response =>
            InsertGeoInfoResponse(response.geoInfo)
        }

    override def insertCollection: Service[InsertCollectionRequest, InsertCollectionResponse] =
      request => {
        val promise = Promise[InsertCollectionResponse]()
        addCollection(toAddCollectionRequest(request)) map {
          response =>
            response.collection map {
              collection => {
                val futures = request.cards map {
                  card =>
                    addCard(toAddCardRequest(collection.id, card))
                }
                Future.sequence(futures) map (p => promise.complete(Success(InsertCollectionResponse(true)))) recover {
                  case _ => promise.complete(Success(InsertCollectionResponse(false)))
                }
              }
            } getOrElse promise.complete(Success(InsertCollectionResponse(true)))
        } recover {
          case _ => promise.complete(Success(InsertCollectionResponse(false)))
        }

        promise.future
      }
  }

}
