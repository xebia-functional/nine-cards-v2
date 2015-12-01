package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toApp
import com.fortysevendeg.ninecardslauncher.repository.commons.IterableCursor.IterableCursorSeq
import com.fortysevendeg.ninecardslauncher.repository.model.{App, AppData}
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import com.fortysevendeg.ninecardslauncher.repository.commons.IterableCursor._

import scalaz.concurrent.Task

class AppRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val appUri = uriCreator.parse(appUriString)

  def addApp(data: AppData): ServiceDef2[App, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            name -> data.name,
            packageName -> data.packageName,
            className -> data.className,
            category -> data.category,
            imagePath -> data.imagePath,
            colorPrimary -> data.colorPrimary,
            dateInstalled -> data.dateInstalled,
            dateUpdate -> data.dateUpdate,
            version -> data.version,
            installedFromGooglePlay -> data.installedFromGooglePlay)

          val id = contentResolverWrapper.insert(
            uri = appUri,
            values = values)

          App(
            id = id,
            data = data)
        }
      }
    }

  def deleteApps(where: String = ""): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = appUri,
            where = where)
        }
      }
    }

  def deleteApp(app: App): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = appUri,
            id = app.id)
        }
      }
    }

  def deleteAppByPackage(packageName: String): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = appUri,
            where = s"${AppEntity.packageName} = ?",
            whereParams = Seq(packageName))
        }
      }
    }

  def fetchApps(orderBy: String = ""): ServiceDef2[Seq[App], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = appUri,
            projection = allFields,
            orderBy = orderBy)(getListFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def fetchIterableApps(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): ServiceDef2[IterableCursorSeq[App], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = appUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(appFromCursor)
        }
      }
    }

  def findAppById(id: Int): ServiceDef2[Option[App], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = appUri,
            id = id,
            projection = allFields)(getEntityFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def fetchAppByPackage(packageName: String): ServiceDef2[Option[App], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetch(
            uri = appUri,
            projection = allFields,
            where = s"${AppEntity.packageName} = ?",
            whereParams = Seq(packageName))(getEntityFromCursor(appEntityFromCursor)) map toApp
        }
      }
    }

  def updateApp(app: App): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            name -> app.data.name,
            packageName -> app.data.packageName,
            className -> app.data.className,
            category -> app.data.category,
            imagePath -> app.data.imagePath,
            colorPrimary -> app.data.colorPrimary,
            dateInstalled -> app.data.dateInstalled,
            dateUpdate -> app.data.dateUpdate,
            version -> app.data.version,
            installedFromGooglePlay -> app.data.installedFromGooglePlay)

          contentResolverWrapper.updateById(
            uri = appUri,
            id = app.id,
            values = values
          )
        }
      }
    }
}
