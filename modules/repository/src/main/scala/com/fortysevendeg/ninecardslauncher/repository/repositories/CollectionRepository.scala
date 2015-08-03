package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCollection
import com.fortysevendeg.ninecardslauncher.repository.RepositoryExceptions.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.commons.{CollectionUri, ContentResolverWrapper, NineCardsUri}
import com.fortysevendeg.ninecardslauncher.repository.model.{Collection, CollectionData}
import com.fortysevendeg.ninecardslauncher.repository.provider.CollectionEntity.{allFields, position, _}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CollectionEntity, DBUtils}

import scalaz.concurrent.Task

class CollectionRepository(contentResolverWrapper: ContentResolverWrapper) extends DBUtils {

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
            appsCategory -> (data.appsCategory getOrElse ""),
            constrains -> (data.constrains getOrElse ""),
            originalSharedCollectionId -> (data.originalSharedCollectionId getOrElse ""),
            sharedCollectionId -> (data.sharedCollectionId getOrElse ""),
            sharedCollectionSubscribed -> (data.sharedCollectionSubscribed getOrElse false))

          val id = contentResolverWrapper.insert(
            nineCardsUri = CollectionUri,
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
            nineCardsUri = CollectionUri,
            id = collection.id)
        }
      }
    }

  def findCollectionById(id: Int): ServiceDef2[Option[Collection], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            nineCardsUri = CollectionUri,
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
            appsCategory -> (collection.data.appsCategory getOrElse ""),
            constrains -> (collection.data.constrains getOrElse ""),
            originalSharedCollectionId -> (collection.data.originalSharedCollectionId getOrElse ""),
            sharedCollectionId -> (collection.data.sharedCollectionId getOrElse ""),
            sharedCollectionSubscribed -> (collection.data.sharedCollectionSubscribed getOrElse false))

          contentResolverWrapper.updateById(
            nineCardsUri = CollectionUri,
            id = collection.id,
            values = values)
        }
      }
    }

  private[this] def fetchCollection(
    nineCardsUri: NineCardsUri = CollectionUri,
    projection: Seq[String] = allFields,
    selection: String = "",
    selectionArgs: Seq[String] = Seq.empty[String],
    sortOrder: String = "") =
    contentResolverWrapper.fetch(
      nineCardsUri = nineCardsUri,
      projection = projection,
      where = selection,
      whereParams = selectionArgs,
      orderBy = sortOrder)(getEntityFromCursor(collectionEntityFromCursor)) map toCollection

  private[this] def fetchCollections(
    nineCardsUri: NineCardsUri = CollectionUri,
    projection: Seq[String] = allFields,
    selection: String = "",
    selectionArgs: Seq[String] = Seq.empty[String],
    sortOrder: String = "") =
    contentResolverWrapper.fetchAll(
      nineCardsUri = nineCardsUri,
      projection = projection,
      where = selection,
      whereParams = selectionArgs,
      orderBy = sortOrder)(getListFromCursor(collectionEntityFromCursor)) map toCollection
}
