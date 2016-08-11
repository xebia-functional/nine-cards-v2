package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService._
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

  def addCollection(data: CollectionData): CatsService[Collection] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(data)

          val id = contentResolverWrapper.insert(
            uri = collectionUri,
            values = values,
            notificationUri = Some(collectionNotificationUri))

          Collection(id = id, data = data)
        }
      }
    }

  def addCollections(datas: Seq[CollectionData]): CatsService[Seq[Collection]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {

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

  def deleteCollections(where: String = ""): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = collectionUri,
            where = where,
            notificationUri = Some(collectionNotificationUri))
        }
      }
    }

  def deleteCollection(collection: Collection): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = collectionUri,
            id = collection.id,
            notificationUri = Some(collectionNotificationUri))
        }
      }
    }

  def findCollectionById(id: Int): CatsService[Option[Collection]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = collectionUri,
            id = id,
            projection = allFields)(getEntityFromCursor(collectionEntityFromCursor)) map toCollection
        }
      }
    }

  def fetchCollectionBySharedCollectionId(sharedCollectionId: String): CatsService[Option[Collection]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          fetchCollection(
            selection = s"$originalSharedCollectionId = ?",
            selectionArgs = Seq(sharedCollectionId.toString))
        }
      }
    }

  def fetchCollectionByPosition(position: Int): CatsService[Option[Collection]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          fetchCollection(selection = s"${CollectionEntity.position} = ?", selectionArgs = Seq(position.toString))
        }
      }
    }

  def fetchIterableCollections(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): CatsService[IterableCursor[Collection]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = collectionUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(collectionFromCursor)
        }
      }
    }

  def fetchSortedCollections: CatsService[Seq[Collection]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          fetchCollections(sortOrder = s"${CollectionEntity.position} asc")
        }
      }
    }

  def updateCollection(collection: Collection): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(collection.data)

          contentResolverWrapper.updateById(
            uri = collectionUri,
            id = collection.id,
            values = values,
            notificationUri = Some(collectionNotificationUri))
        }
      }
    }

  def updateCollections(collections: Seq[Collection]): CatsService[Seq[Int]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
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
