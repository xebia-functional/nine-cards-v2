package com.fortysevendeg.ninecardslauncher.modules.repository.impl

import android.content.ContentResolver
import com.fortysevendeg.ninecardslauncher.commons.ContextWrapperProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.repository.Conversions
import com.fortysevendeg.ninecardslauncher.modules.repository._
import com.fortysevendeg.ninecardslauncher.repository._

import scala.concurrent.{Future, Promise, ExecutionContext}

trait RepositoryServicesComponentImpl
  extends RepositoryServicesComponent {

  self : ContextWrapperProvider =>

  lazy val repositoryServices = new RepositoryServicesImpl

  class RepositoryServicesImpl
    extends RepositoryServices
    with Conversions
    with NineCardRepositoryClient {

    override implicit val contentResolver: ContentResolver = contextProvider.application.getContentResolver

    override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

    override def getCollections: Service[GetCollectionsRequest, GetCollectionsResponse] =
      request => {
        val promise = Promise[GetCollectionsResponse]()
        getSortedCollections(GetSortedCollectionsRequest()) map {
          response =>
            val futures = toCollectionSeq(response.collections) map {
              collection =>
                getCardByCollection(GetAllCardsByCollectionRequest(collection.id)) map {
                  cardResponse =>
                    collection.copy(cards = cardResponse.result map toCard)
                }
            }
            Future.sequence(futures) map {
              collections =>
                promise.success(GetCollectionsResponse(collections))
            } recover {
              case _ => promise.success(GetCollectionsResponse(Seq.empty))
            }
        } recover {
          case _ => promise.success(GetCollectionsResponse(Seq.empty))
        }
        promise.future
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
                Future.sequence(futures) map (p => promise.success(InsertCollectionResponse(true))) recover {
                  case _ => promise.success(InsertCollectionResponse(false))
                }
              }
            } getOrElse promise.success(InsertCollectionResponse(true))
        } recover {
          case _ => promise.success(InsertCollectionResponse(false))
        }

        promise.future
      }
  }

}
