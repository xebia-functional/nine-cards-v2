package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toDockApp
import com.fortysevendeg.ninecardslauncher.repository.model.{DockApp, DockAppData}
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.provider.DockAppEntity._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}

import scalaz.concurrent.Task

class DockAppRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val dockAppUri = uriCreator.parse(dockAppUriString)

  def addDockApp(data: DockAppData): ServiceDef2[DockApp, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            name -> data.name,
            cardType -> data.cardType,
            collectionId -> (data.collectionId orNull),
            intent -> data.intent,
            imagePath -> data.imagePath,
            position -> data.position)

          val id = contentResolverWrapper.insert(
            uri = dockAppUri,
            values = values)

          DockApp(id = id, data = data)
        }
      }
    }

  def deleteDockApp(dockApp: DockApp): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = dockAppUri,
            id = dockApp.id)
        }
      }
    }

  def findDockAppById(id: Int): ServiceDef2[Option[DockApp], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = dockAppUri,
            id = id,
            projection = allFields)(getEntityFromCursor(dockAppEntityFromCursor)) map toDockApp
        }
      }
    }

  def fetchDockApps: ServiceDef2[Seq[DockApp], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = dockAppUri,
            projection = allFields)(getListFromCursor(dockAppEntityFromCursor)) map toDockApp
        }
      }
    }

  def updateDockApp(item: DockApp): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            name -> item.data.name,
            cardType -> item.data.cardType,
            collectionId -> (item.data.collectionId orNull),
            intent -> item.data.intent,
            imagePath -> item.data.imagePath,
            position -> item.data.position)

          contentResolverWrapper.updateById(
            uri = dockAppUri,
            id = item.id,
            values = values)
        }
      }
    }
}
