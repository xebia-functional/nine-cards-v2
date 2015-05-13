package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{CacheCategoryUri, ContentResolverWrapperComponent}
import com.fortysevendeg.ninecardslauncher.provider.CacheCategoryEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCacheCategory
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.CacheCategory
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.util.control.NonFatal

trait CacheCategoryRepositoryClient extends DBUtils {

  self: ContentResolverWrapperComponent =>

  implicit val executionContext: ExecutionContext

  def addCacheCategory: Service[AddCacheCategoryRequest, AddCacheCategoryResponse] =
    request =>
      tryToFuture {
        Try {
          val values = Map[String, Any](
            PackageName -> request.data.packageName,
            Category -> request.data.category,
            StarRating -> request.data.starRating,
            NumDownloads -> request.data.numDownloads,
            RatingsCount -> request.data.ratingsCount,
            CommentCount -> request.data.commentCount)

          val id = contentResolverWrapper.insert(
            nineCardsUri = CacheCategoryUri,
            values = values)

          AddCacheCategoryResponse(
            cacheCategory = Some(CacheCategory(
              id = id,
              data = request.data)))

        } recover {
          case NonFatal(e) => throw RepositoryInsertException()
        }
      }

  def deleteCacheCategory: Service[DeleteCacheCategoryRequest, DeleteCacheCategoryResponse] =
    request =>
      tryToFuture {
        Try {
          val deleted = contentResolverWrapper.deleteById(
            nineCardsUri = CacheCategoryUri,
            id = request.cacheCategory.id)

          DeleteCacheCategoryResponse(deleted = deleted)

        } recover {
          case NonFatal(e) => throw RepositoryDeleteException()
        }
      }

  def deleteCacheByPackageCategory: Service[DeleteCacheCategoryByPackageRequest, DeleteCacheCategoryByPackageResponse] =
    request =>
      tryToFuture {
        Try {
          val deleted = contentResolverWrapper.delete(
            nineCardsUri = CacheCategoryUri,
            where = s"$PackageName = ?",
            whereParams = Seq(request.`package`))

          DeleteCacheCategoryByPackageResponse(deleted = deleted)

        } recover {
          case NonFatal(e) => throw RepositoryDeleteException()
        }
      }

  def getAllCacheCategories: Service[GetAllCacheCategoriesRequest, GetAllCacheCategoriesResponse] =
    request =>
      tryToFuture {
        Try {
          val cacheCategories = contentResolverWrapper.fetchAll(
            nineCardsUri = CacheCategoryUri,
            projection = AllFields)(getListFromCursor(cacheCategoryEntityFromCursor)) map toCacheCategory

          GetAllCacheCategoriesResponse(cacheCategories)
        } recover {
          case e: Exception =>
            GetAllCacheCategoriesResponse(cacheCategories = Seq.empty)
        }
      }

  def getCacheCategoryById: Service[GetCacheCategoryByIdRequest, GetCacheCategoryByIdResponse] =
    request =>
      tryToFuture {
        Try {
          val cacheCategory = contentResolverWrapper.findById(
            nineCardsUri = CacheCategoryUri,
            id = request.id,
            projection = AllFields)(getEntityFromCursor(cacheCategoryEntityFromCursor)) map toCacheCategory

          GetCacheCategoryByIdResponse(cacheCategory)
        } recover {
          case e: Exception =>
            GetCacheCategoryByIdResponse(result = None)
        }
      }

  def getCacheCategoryByPackage: Service[GetCacheCategoryByPackageRequest, GetCacheCategoryByPackageResponse] =
    request =>
      tryToFuture {
        Try {
          val cacheCategory = contentResolverWrapper.fetch(
            nineCardsUri = CacheCategoryUri,
            projection = AllFields,
            where = s"$PackageName = ?",
            whereParams = Seq(request.`package`))(getEntityFromCursor(cacheCategoryEntityFromCursor)) map toCacheCategory


          GetCacheCategoryByPackageResponse(cacheCategory)
        } recover {
          case e: Exception =>
            GetCacheCategoryByPackageResponse(result = None)
        }
      }

  def updateCacheCategory: Service[UpdateCacheCategoryRequest, UpdateCacheCategoryResponse] =
    request =>
      tryToFuture {
        Try {
          val values = Map[String, Any](
            PackageName -> request.cacheCategory.data.packageName,
            Category -> request.cacheCategory.data.category,
            StarRating -> request.cacheCategory.data.starRating,
            NumDownloads -> request.cacheCategory.data.numDownloads,
            RatingsCount -> request.cacheCategory.data.ratingsCount,
            CommentCount -> request.cacheCategory.data.commentCount)

          val updated = contentResolverWrapper.updateById(
            nineCardsUri = CacheCategoryUri,
            id = request.cacheCategory.id,
            values = values
          )

          UpdateCacheCategoryResponse(updated = updated)

        } recover {
          case NonFatal(e) => throw RepositoryUpdateException()
        }
      }
}
