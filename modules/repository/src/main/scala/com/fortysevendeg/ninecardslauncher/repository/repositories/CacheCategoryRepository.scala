package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCacheCategory
import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategory, CacheCategoryData}
import com.fortysevendeg.ninecardslauncher.repository.provider.CacheCategoryEntity
import com.fortysevendeg.ninecardslauncher.repository.provider.CacheCategoryEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}

import scalaz.concurrent.Task

class CacheCategoryRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val cacheCategoryUri = uriCreator.parse(cacheCategoryUriString)

  def addCacheCategory(data: CacheCategoryData): ServiceDef2[CacheCategory, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            packageName -> data.packageName,
            category -> data.category,
            starRating -> data.starRating,
            numDownloads -> data.numDownloads,
            ratingsCount -> data.ratingsCount,
            commentCount -> data.commentCount)

          val id = contentResolverWrapper.insert(
            uri = cacheCategoryUri,
            values = values)

          CacheCategory(
            id = id,
            data = data)
        }
      }
    }

  def deleteCacheCategory(cacheCategory: CacheCategory): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = cacheCategoryUri,
            id = cacheCategory.id)
        }
      }
    }

  def deleteCacheCategoryByPackage(packageName: String): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = cacheCategoryUri,
            where = s"${CacheCategoryEntity.packageName} = ?",
            whereParams = Seq(packageName))
        }
      }
    }

  def fetchCacheCategories: ServiceDef2[Seq[CacheCategory], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = cacheCategoryUri,
            projection = allFields)(getListFromCursor(cacheCategoryEntityFromCursor)) map toCacheCategory
        }
      }
    }

  def findCacheCategoryById(id: Int): ServiceDef2[Option[CacheCategory], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = cacheCategoryUri,
            id = id,
            projection = allFields)(getEntityFromCursor(cacheCategoryEntityFromCursor)) map toCacheCategory
        }
      }
    }

  def fetchCacheCategoryByPackage(packageName: String): ServiceDef2[Option[CacheCategory], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetch(
            uri = cacheCategoryUri,
            projection = allFields,
            where = s"${CacheCategoryEntity.packageName} = ?",
            whereParams = Seq(packageName))(getEntityFromCursor(cacheCategoryEntityFromCursor)) map toCacheCategory
        }
      }
    }

  def updateCacheCategory(cacheCategory: CacheCategory): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            packageName -> cacheCategory.data.packageName,
            category -> cacheCategory.data.category,
            starRating -> cacheCategory.data.starRating,
            numDownloads -> cacheCategory.data.numDownloads,
            ratingsCount -> cacheCategory.data.ratingsCount,
            commentCount -> cacheCategory.data.commentCount)

          contentResolverWrapper.updateById(
            uri = cacheCategoryUri,
            id = cacheCategory.id,
            values = values
          )
        }
      }
    }
}
