package cards.nine.repository.repositories

import android.net.Uri
import cards.nine.commons.CatchAll
import cards.nine.commons.contentresolver.Conversions._
import cards.nine.commons.contentresolver.IterableCursor._
import cards.nine.commons.contentresolver.NotificationUri._
import cards.nine.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.repository.Conversions.toCollection
import cards.nine.repository.model.{Collection, CollectionData}
import cards.nine.repository.provider.CollectionEntity.{allFields, position, _}
import cards.nine.repository.provider.NineCardsUri._
import cards.nine.repository.provider.{CollectionEntity, NineCardsUri}
import cards.nine.repository.repositories.RepositoryUtils._
import cards.nine.repository.{ImplicitsRepositoryExceptions, RepositoryException}

import scala.language.postfixOps

class CollectionRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val collectionUri = uriCreator.parse(collectionUriString)

  val collectionNotificationUri = uriCreator.parse(s"$baseUriNotificationString/$collectionUriPath")

  def addCollection(data: CollectionData): TaskService[Collection] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(data)

        val id = contentResolverWrapper.insert(
          uri = collectionUri,
          values = values,
          notificationUris = Seq(collectionNotificationUri))

        Collection(id = id, data = data)
      }
    }

  def addCollections(datas: Seq[CollectionData]): TaskService[Seq[Collection]] =
    TaskService {
      CatchAll[RepositoryException] {

        val values = datas map createMapValues

        val ids = contentResolverWrapper.inserts(
          authority = NineCardsUri.authorityPart,
          uri = collectionUri,
          allValues = values,
          notificationUris = Seq(collectionNotificationUri))

        datas zip ids map {
          case (data, id) => Collection(id = id, data = data)
        }
      }
    }

  def deleteCollections(where: String = ""): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.delete(
          uri = collectionUri,
          where = where,
          notificationUris = Seq(collectionNotificationUri))
      }
    }

  def deleteCollection(collection: Collection): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.deleteById(
          uri = collectionUri,
          id = collection.id,
          notificationUris = Seq(collectionNotificationUri))
      }
    }

  def findCollectionById(id: Int): TaskService[Option[Collection]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.findById(
          uri = collectionUri,
          id = id,
          projection = allFields)(getEntityFromCursor(collectionEntityFromCursor)) map toCollection
      }
    }

  def fetchCollectionBySharedCollectionId(id: String): TaskService[Option[Collection]] =
    TaskService {
      CatchAll[RepositoryException] {
        fetchCollection(
          selection = s"$sharedCollectionId = ?",
          selectionArgs = Seq(id.toString))
      }
    }

  def fetchCollectionsBySharedCollectionIds(ids: Seq[String]): TaskService[Seq[Collection]] =
    TaskService {
        CatchAll[RepositoryException] {
          fetchCollections(selection = s"$sharedCollectionId IN (${ids.map(id => s"'$id'").mkString(",")})")
        }
    }

  def fetchCollectionsByCategory(category: String): TaskService[Seq[Collection]] =
    TaskService {
      CatchAll[RepositoryException] {
        fetchCollections(
          selection = s"$appsCategory = ?",
          selectionArgs = Seq(category))
      }
    }

  def fetchCollectionByPosition(position: Int): TaskService[Option[Collection]] =
    TaskService {
      CatchAll[RepositoryException] {
        fetchCollection(selection = s"${CollectionEntity.position} = ?", selectionArgs = Seq(position.toString))
      }
    }

  def fetchIterableCollections(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): TaskService[IterableCursor[Collection]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.getCursor(
          uri = collectionUri,
          projection = allFields,
          where = where,
          whereParams = whereParams,
          orderBy = orderBy).toIterator(collectionFromCursor)
      }
    }

  def fetchSortedCollections: TaskService[Seq[Collection]] =
    TaskService {
      CatchAll[RepositoryException] {
        fetchCollections(sortOrder = s"${CollectionEntity.position} asc")
      }
    }

  def updateCollection(collection: Collection): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(collection.data)

        contentResolverWrapper.updateById(
          uri = collectionUri,
          id = collection.id,
          values = values,
          notificationUris = Seq(collectionNotificationUri))
      }
    }

  def updateCollections(collections: Seq[Collection]): TaskService[Seq[Int]] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = collections map { collection =>
          (collection.id, createMapValues(collection.data))
        }

        contentResolverWrapper.updateByIds(
          authority = NineCardsUri.authorityPart,
          uri = collectionUri,
          idAndValues = values,
          notificationUris = Seq(collectionNotificationUri))
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
