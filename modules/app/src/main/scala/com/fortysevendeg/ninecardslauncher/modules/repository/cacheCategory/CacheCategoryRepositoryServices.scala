package com.fortysevendeg.ninecardslauncher.modules.repository.cacheCategory

import android.content.ContentResolver
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.repository.{FetchCacheCategoriesRequest => RepoFetchCacheCategoriesRequest, NineCardRepositoryClient}

import scala.concurrent.ExecutionContext

trait CacheCategoryRepositoryServices {
  def addCacheCategory: Service[AddCacheCategoryRequest, AddCacheCategoryResponse]
  def deleteCacheCategory: Service[DeleteCacheCategoryRequest, DeleteCacheCategoryResponse]
  def deleteCacheCategoryByPackage: Service[DeleteCacheCategoryByPackageRequest, DeleteCacheCategoryByPackageResponse]
  def fetchCacheCategoryByPackage: Service[FetchCacheCategoryByPackageRequest, FetchCacheCategoryByPackageResponse]
  def fetchCacheCategories: Service[FetchCacheCategoriesRequest, FetchCacheCategoriesResponse]
  def findCacheCategoryById: Service[FindCacheCategoryByIdRequest, FindCacheCategoryByIdResponse]
  def updateCacheCategory: Service[UpdateCacheCategoryRequest, UpdateCacheCategoryResponse]
}

trait CacheCategoryRepositoryServicesComponent {
  val cacheCategoryRepositoryServices: CacheCategoryRepositoryServices
}

trait CacheCategoryRepositoryServicesComponentImpl
    extends CacheCategoryRepositoryServicesComponent {

  self: ContextWrapperProvider =>

  lazy val cacheCategoryRepositoryServices = new CacheCategoryRepositoryServicesImpl

  class CacheCategoryRepositoryServicesImpl
      extends CacheCategoryRepositoryServices
      with Conversions
      with NineCardRepositoryClient {

    override implicit val contentResolver: ContentResolver = contextProvider.application.getContentResolver
    override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

    override def addCacheCategory: Service[AddCacheCategoryRequest, AddCacheCategoryResponse] =
      request => {
        repoAddCacheCategory(toRepositoryAddCacheCategoryRequest(request)) map {
          response =>
            AddCacheCategoryResponse(toCacheCategory(response.cacheCategory))
        }
      }

    override def deleteCacheCategory: Service[DeleteCacheCategoryRequest, DeleteCacheCategoryResponse] =
      request => {
        repoDeleteCacheCategory(toRepositoryDeleteCacheCategoryRequest(request)) map {
          response =>
            DeleteCacheCategoryResponse(response.deleted)
        }
      }

    override def deleteCacheCategoryByPackage: Service[DeleteCacheCategoryByPackageRequest, DeleteCacheCategoryByPackageResponse] =
      request => {
        repoDeleteCacheCategoryByPackage(toRepositoryDeleteCacheCategoryByPackageRequest(request)) map {
          response =>
            DeleteCacheCategoryByPackageResponse(response.deleted)
        }
      }

    override def fetchCacheCategoryByPackage: Service[FetchCacheCategoryByPackageRequest, FetchCacheCategoryByPackageResponse] =
      request => {
        repoFetchCacheCategoryByPackage(toRepositoryFetchCacheCategoryByPackageRequest(request)) map {
          response =>
            FetchCacheCategoryByPackageResponse(response.category map toCacheCategory)
        }
      }

    override def fetchCacheCategories: Service[FetchCacheCategoriesRequest, FetchCacheCategoriesResponse] =
      request => {
        repoFetchCacheCategories(RepoFetchCacheCategoriesRequest()) map {
          response =>
            FetchCacheCategoriesResponse(toCacheCategorySeq(response.cacheCategories))
        }
      }

    override def findCacheCategoryById: Service[FindCacheCategoryByIdRequest, FindCacheCategoryByIdResponse] =
      request => {
        repoFindCacheCategoryById(toRepositoryFindCacheCategoryByIdRequest(request)) map {
          response =>
            FindCacheCategoryByIdResponse(response.category map toCacheCategory)
        }
      }

    override def updateCacheCategory: Service[UpdateCacheCategoryRequest, UpdateCacheCategoryResponse] =
      request =>
        repoUpdateCacheCategory(toRepositoryUpdateCacheCategoryRequest(request)) map {
          response =>
            UpdateCacheCategoryResponse(updated = response.updated)
        }
  }

}