package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toDockApp
import com.fortysevendeg.ninecardslauncher.repository.model.{DockApp, DockAppData}
import com.fortysevendeg.ninecardslauncher.repository.provider.{DockAppEntity, NineCardsUri}
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.provider.DockAppEntity._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import IterableCursor._

class DockAppRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val dockAppUri = uriCreator.parse(dockAppUriString)

  val dockAppNotificationUri = uriCreator.parse(s"$baseUriNotificationString/$dockAppUriPath")

  def addDockApp(data: DockAppData): TaskService[DockApp] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(data)

        val id = contentResolverWrapper.insert(
          uri = dockAppUri,
          values = values,
          notificationUris = Seq(dockAppNotificationUri))

        DockApp(id = id, data = data)
      }
    }

  def addDockApps(datas: Seq[DockAppData]): TaskService[Seq[DockApp]] =
    TaskService {
      CatchAll[RepositoryException] {

        val values = datas map createMapValues

        val ids = contentResolverWrapper.inserts(
          authority = NineCardsUri.authorityPart,
          uri = dockAppUri,
          allValues = values,
          notificationUris = Seq(dockAppNotificationUri))

        datas zip ids map {
          case (data, id) => DockApp(id = id, data = data)
        }
      }
    }

  def deleteDockApps(where: String = ""): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.delete(
          uri = dockAppUri,
          where = where,
          notificationUris = Seq(dockAppNotificationUri))
      }
    }

  def deleteDockApp(dockApp: DockApp): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.deleteById(
          uri = dockAppUri,
          id = dockApp.id,
          notificationUris = Seq(dockAppNotificationUri))
      }
    }

  def findDockAppById(id: Int): TaskService[Option[DockApp]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.findById(
          uri = dockAppUri,
          id = id,
          projection = allFields)(getEntityFromCursor(dockAppEntityFromCursor)) map toDockApp
      }
    }

  def fetchDockApps(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = s"${DockAppEntity.position} asc"): TaskService[Seq[DockApp]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.fetchAll(
          uri = dockAppUri,
          projection = allFields,
          where = where,
          whereParams = whereParams,
          orderBy = orderBy)(getListFromCursor(dockAppEntityFromCursor)) map toDockApp
      }
    }

  def fetchIterableDockApps(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = s"${DockAppEntity.position} asc"): TaskService[IterableCursor[DockApp]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.getCursor(
          uri = dockAppUri,
          projection = allFields,
          where = where,
          whereParams = whereParams,
          orderBy = orderBy).toIterator(dockAppFromCursor)
      }
    }

  def updateDockApp(item: DockApp): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(item.data)

        contentResolverWrapper.updateById(
          uri = dockAppUri,
          id = item.id,
          values = values,
          notificationUris = Seq(dockAppNotificationUri))
      }
    }

  def updateDockApps(items: Seq[DockApp]): TaskService[Seq[Int]] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = items map { item =>
          (item.id, createMapValues(item.data))
        }

        contentResolverWrapper.updateByIds(
          authority = NineCardsUri.authorityPart,
          uri = dockAppUri,
          idAndValues = values)
      }
    }

  private[this] def createMapValues(data: DockAppData) = Map[String, Any](
    name -> data.name,
    dockType -> data.dockType,
    intent -> data.intent,
    imagePath -> data.imagePath,
    position -> data.position)
}
