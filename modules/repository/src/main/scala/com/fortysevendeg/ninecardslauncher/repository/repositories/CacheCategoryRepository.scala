package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCacheCategory
import com.fortysevendeg.ninecardslauncher.repository.commons.{CacheCategoryUri, ContentResolverWrapper}
import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategory, CacheCategoryData}
import com.fortysevendeg.ninecardslauncher.repository.provider.CacheCategoryEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.{CacheCategoryEntity, DBUtils}

import scalaz.\/
import scalaz.concurrent.Task

class CacheCategoryRepository(contentResolverWrapper: ContentResolverWrapper) extends DBUtils {

  def addCacheCategory(data: CacheCategoryData): Task[NineCardsException \/ CacheCategory] =
    Task {
      fromTryCatchNineCardsException[CacheCategory] {
        val values = Map[String, Any](
          packageName -> data.packageName,
          category -> data.category,
          starRating -> data.starRating,
          numDownloads -> data.numDownloads,
          ratingsCount -> data.ratingsCount,
          commentCount -> data.commentCount)

        val id = contentResolverWrapper.insert(
          nineCardsUri = CacheCategoryUri,
          values = values)

        CacheCategory(
          id = id,
          data = data)
      }
    }

  def deleteCacheCategory(cacheCategory: CacheCategory): Task[NineCardsException \/ Int] =
    Task {
      fromTryCatchNineCardsException[Int] {
        contentResolverWrapper.deleteById(
          nineCardsUri = CacheCategoryUri,
          id = cacheCategory.id)
      }
    }

  def deleteCacheCategoryByPackage(packageName: String): Task[NineCardsException \/ Int] =
    Task {
      fromTryCatchNineCardsException[Int] {
        contentResolverWrapper.delete(
          nineCardsUri = CacheCategoryUri,
          where = s"${CacheCategoryEntity.packageName} = ?",
          whereParams = Seq(packageName))
      }
    }

  def fetchCacheCategories: Task[NineCardsException \/ Seq[CacheCategory]] =
    Task {
      fromTryCatchNineCardsException[Seq[CacheCategory]] {
        contentResolverWrapper.fetchAll(
          nineCardsUri = CacheCategoryUri,
          projection = allFields)(getListFromCursor(cacheCategoryEntityFromCursor)) map toCacheCategory
      }
    }

  def findCacheCategoryById(id: Int): Task[NineCardsException \/ Option[CacheCategory]] =
    Task {
      fromTryCatchNineCardsException[Option[CacheCategory]] {
        contentResolverWrapper.findById(
          nineCardsUri = CacheCategoryUri,
          id = id,
          projection = allFields)(getEntityFromCursor(cacheCategoryEntityFromCursor)) map toCacheCategory
      }
    }

  def fetchCacheCategoryByPackage(packageName: String): Task[NineCardsException \/ Option[CacheCategory]] =
    Task {
      fromTryCatchNineCardsException[Option[CacheCategory]] {
        contentResolverWrapper.fetch(
          nineCardsUri = CacheCategoryUri,
          projection = allFields,
          where = s"${CacheCategoryEntity.packageName} = ?",
          whereParams = Seq(packageName))(getEntityFromCursor(cacheCategoryEntityFromCursor)) map toCacheCategory
      }
    }

  def updateCacheCategory(cacheCategory: CacheCategory): Task[NineCardsException \/ Int] =
    Task {
      fromTryCatchNineCardsException[Int] {
        val values = Map[String, Any](
          packageName -> cacheCategory.data.packageName,
          category -> cacheCategory.data.category,
          starRating -> cacheCategory.data.starRating,
          numDownloads -> cacheCategory.data.numDownloads,
          ratingsCount -> cacheCategory.data.ratingsCount,
          commentCount -> cacheCategory.data.commentCount)

        contentResolverWrapper.updateById(
          nineCardsUri = CacheCategoryUri,
          id = cacheCategory.id,
          values = values
        )
      }
    }
}
