package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCollection
import com.fortysevendeg.ninecardslauncher.repository.model.{Collection, CollectionData}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CollectionEntity, NineCardsUri}
import com.fortysevendeg.ninecardslauncher.repository.provider.CollectionEntity.{allFields, position, _}
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.repositories.RepositoryUtils._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}

import scala.language.postfixOps
import scalaz.concurrent.Task

class CollectionRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val collectionUri = uriCreator.parse(collectionUriString)

  val collectionNotificationUri = uriCreator.parse(collectionUriNotificationString)

  def addCollection(data: CollectionData): ServiceDef2[Collection, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = createMapValues(data)

          val id = contentResolverWrapper.insert(
            uri = collectionUri,
            values = values,
            notificationUri = Some(collectionNotificationUri))

          Collection(id = id, data = data)
        }
      }
    }

  def addCollections(datas: Seq[CollectionData]): ServiceDef2[Seq[Collection], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {

          val values = datas map createMapValues

          val ids = contentResolverWrapper.inserts(
            authority = NineCardsUri.authorityPart,
            uri = collectionUri,
            allValues = values,
            notificationUri = Some(collectionNotificationUri))

          datas zip ids map {
            case (data, id) => Collection(id = id, data = data)
          }
        }
      }
    }

  def deleteCollections(where: String = ""): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = collectionUri,
            where = where,
            notificationUri = Some(collectionNotificationUri))
        }
      }
    }

  def deleteCollection(collection: Collection): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = collectionUri,
            id = collection.id,
            notificationUri = Some(collectionNotificationUri))
        }
      }
    }

  def findCollectionById(id: Int): ServiceDef2[Option[Collection], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = collectionUri,
            id = id,
            projection = allFields)(getEntityFromCursor(collectionEntityFromCursor)) map toCollection
        }
      }
    }

  def fetchCollectionBySharedCollectionId(id: String): ServiceDef2[Option[Collection], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          fetchCollection(
            selection = s"$sharedCollectionId = ?",
            selectionArgs = Seq(id.toString))
        }
      }
    }

  def fetchCollectionByOriginalSharedCollectionId(sharedCollectionId: String): ServiceDef2[Option[Collection], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          fetchCollection(
            selection = s"$originalSharedCollectionId = ?",
            selectionArgs = Seq(sharedCollectionId.toString))
        }
      }
    }

  def fetchCollectionByPosition(position: Int): ServiceDef2[Option[Collection], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          fetchCollection(selection = s"${CollectionEntity.position} = ?", selectionArgs = Seq(position.toString))
        }
      }
    }

  def fetchIterableCollections(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): ServiceDef2[IterableCursor[Collection], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = collectionUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(collectionFromCursor)
        }
      }
    }

  def fetchSortedCollections: ServiceDef2[Seq[Collection], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          fetchCollections(sortOrder = s"${CollectionEntity.position} asc")
        }
      }
    }

  def updateCollection(collection: Collection): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = createMapValues(collection.data)

          contentResolverWrapper.updateById(
            uri = collectionUri,
            id = collection.id,
            values = values,
            notificationUri = Some(collectionNotificationUri))
        }
      }
    }

  def updateCollections(collections: Seq[Collection]): ServiceDef2[Seq[Int], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = collections map { collection =>
            (collection.id, createMapValues(collection.data))
          }

          contentResolverWrapper.updateByIds(
            authority = NineCardsUri.authorityPart,
            uri = collectionUri,
            idAndValues = values,
            notificationUri = Some(collectionNotificationUri))
        }
      }
    }

  private[this] def fetchCollection(
    uri: Uri = collectionUri,
    projection: Seq[String] = allFields,
    selection: String = "",
    selectionArgs: Seq[String] = Seq.empty[String],
    sortOrder: String = "") =
    contentResolverWrapper.fetch(
      uri = uri,
      projection = projection,
      where = selection,
      whereParams = selectionArgs,
      orderBy = sortOrder)(getEntityFromCursor(collectionEntityFromCursor)) map toCollection

  private[this] def fetchCollections(
    uri: Uri = collectionUri,
    projection: Seq[String] = allFields,
    selection: String = "",
    selectionArgs: Seq[String] = Seq.empty[String],
    sortOrder: String = "") =
    contentResolverWrapper.fetchAll(
      uri = uri,
      projection = projection,
      where = selection,
      whereParams = selectionArgs,
      orderBy = sortOrder)(getListFromCursor(collectionEntityFromCursor)) map toCollection

  private[this] def createMapValues(data: CollectionData) = Map[String, Any](
    position -> data.position,
    name -> data.name,
    collectionType -> data.collectionType,
    icon -> data.icon,
    themedColorIndex -> data.themedColorIndex,
    appsCategory -> flatOrNull(data.appsCategory),
    originalSharedCollectionId -> flatOrNull(data.originalSharedCollectionId),
    sharedCollectionId -> flatOrNull(data.sharedCollectionId),
    sharedCollectionSubscribed -> data.sharedCollectionSubscribed.orNull)

}
