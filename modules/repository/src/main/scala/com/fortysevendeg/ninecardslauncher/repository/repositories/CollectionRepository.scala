package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCollection
import com.fortysevendeg.ninecardslauncher.repository.commons.{CollectionUri, ContentResolverWrapper, NineCardsUri}
import com.fortysevendeg.ninecardslauncher.repository.model.{Collection, CollectionData}
import com.fortysevendeg.ninecardslauncher.repository.provider.CollectionEntity.{allFields, position, _}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CollectionEntity, DBUtils}

import scalaz.\/
import scalaz.concurrent.Task

class CollectionRepository(contentResolverWrapper: ContentResolverWrapper) extends DBUtils {

  def addCollection(data: CollectionData): Task[NineCardsException \/ Collection] =
    Task {
      fromTryCatchNineCardsException[Collection] {
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

  def deleteCollection(collection: Collection): Task[NineCardsException \/ Int] =
    Task {
      fromTryCatchNineCardsException[Int] {
        contentResolverWrapper.deleteById(
          nineCardsUri = CollectionUri,
          id = collection.id)
      }
    }

  def findCollectionById(id: Int): Task[NineCardsException \/ Option[Collection]] =
    Task {
      fromTryCatchNineCardsException[Option[Collection]] {
        contentResolverWrapper.findById(
          nineCardsUri = CollectionUri,
          id = id,
          projection = allFields)(getEntityFromCursor(collectionEntityFromCursor)) map toCollection
      }
    }

  def fetchCollectionBySharedCollectionId(sharedCollectionId: String): Task[NineCardsException \/ Option[Collection]] =
    Task {
      fromTryCatchNineCardsException[Option[Collection]] {
        fetchCollection(
          selection = s"$originalSharedCollectionId = ?",
          selectionArgs = Seq(sharedCollectionId.toString))
      }
    }

  def fetchCollectionByPosition(position: Int): Task[NineCardsException \/ Option[Collection]] =
    Task {
      fromTryCatchNineCardsException[Option[Collection]] {
        fetchCollection(selection = s"${CollectionEntity.position} = ?", selectionArgs = Seq(position.toString))
      }
    }

  def fetchSortedCollections: Task[NineCardsException \/ Seq[Collection]] =
    Task {
      fromTryCatchNineCardsException[Seq[Collection]] {
        fetchCollections(sortOrder = s"${CollectionEntity.position} asc")
      }
    }

  def updateCollection(collection: Collection): Task[NineCardsException \/ Int] =
    Task {
      fromTryCatchNineCardsException[Int] {
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

  private[this] def fetchCollection(
    nineCardsUri: NineCardsUri = CollectionUri,
    projection: Seq[String] = allFields,
    selection: String = "",
    selectionArgs: Seq[String] = Seq.empty[String],
    sortOrder: String = ""
    ) =
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
    sortOrder: String = ""
    ) =
    contentResolverWrapper.fetchAll(
      nineCardsUri = nineCardsUri,
      projection = projection,
      where = selection,
      whereParams = selectionArgs,
      orderBy = sortOrder)(getListFromCursor(collectionEntityFromCursor)) map toCollection
}
