package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCollection
import com.fortysevendeg.ninecardslauncher.repository.model.{Collection, CollectionData}
import com.fortysevendeg.ninecardslauncher.repository.provider.CollectionEntity
import com.fortysevendeg.ninecardslauncher.repository.provider.CollectionEntity.{allFields, position, _}
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import RepositoryUtils._
import scalaz.concurrent.Task

class CollectionRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val collectionUri = uriCreator.parse(collectionUriString)

  def addCollection(data: CollectionData): ServiceDef2[Collection, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            position -> data.position,
            name -> data.name,
            collectionType -> data.collectionType,
            icon -> data.icon,
            themedColorIndex -> data.themedColorIndex,
            appsCategory -> flatOrNull(data.appsCategory),
            constrains -> flatOrNull(data.constrains),
            originalSharedCollectionId -> flatOrNull(data.originalSharedCollectionId),
            sharedCollectionId -> flatOrNull(data.sharedCollectionId),
            sharedCollectionSubscribed -> (data.sharedCollectionSubscribed orNull))

          val id = contentResolverWrapper.insert(
            uri = collectionUri,
            values = values)

          Collection(id = id, data = data)
        }
      }
    }

  def deleteCollection(collection: Collection): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = collectionUri,
            id = collection.id)
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

  def fetchCollectionBySharedCollectionId(sharedCollectionId: String): ServiceDef2[Option[Collection], RepositoryException] =
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
          val values = Map[String, Any](
            position -> collection.data.position,
            name -> collection.data.name,
            collectionType -> collection.data.collectionType,
            icon -> collection.data.icon,
            themedColorIndex -> collection.data.themedColorIndex,
            appsCategory -> (collection.data.appsCategory orNull),
            constrains -> (collection.data.constrains orNull),
            originalSharedCollectionId -> (collection.data.originalSharedCollectionId orNull),
            sharedCollectionId -> (collection.data.sharedCollectionId orNull),
            sharedCollectionSubscribed -> (collection.data.sharedCollectionSubscribed orNull))

          contentResolverWrapper.updateById(
            uri = collectionUri,
            id = collection.id,
            values = values)
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
}
