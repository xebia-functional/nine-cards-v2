package com.fortysevendeg.ninecardslauncher.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.provider.CollectionEntity._

case class CollectionEntity(id: Int, data: CollectionEntityData)

case class CollectionEntityData(
    position: Int,
    name: String,
    `type`: String,
    icon: String,
    themedColorIndex: Int,
    appsCategory: String,
    constrains: String,
    originalSharedCollectionId: String,
    sharedCollectionId: String,
    sharedCollectionSubscribed: Boolean)

object CollectionEntity {
  val Table = "Collection"
  val Position = "position"
  val Name = "name"
  val Type = "type"
  val Icon = "icon"
  val ThemedColorIndex = "themedColorIndex"
  val AppsCategory = "appsCategory"
  val Constrains = "constrains"
  val OriginalSharedCollectionId = "originalSharedCollectionId"
  val SharedCollectionId = "sharedCollectionId"
  val SharedCollectionSubscribed = "sharedCollectionSubscribed"

  val AllFields = Seq[String](
    NineCardsSqlHelper.Id,
    Position,
    Name,
    Type,
    Icon,
    ThemedColorIndex,
    AppsCategory,
    Constrains,
    OriginalSharedCollectionId,
    SharedCollectionId,
    SharedCollectionSubscribed)

  def collectionEntityFromCursor(cursor: Cursor) =
    CollectionEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.Id)),
      data = CollectionEntityData(
        position = cursor.getInt(cursor.getColumnIndex(Position)),
        name = cursor.getString(cursor.getColumnIndex(Name)),
        `type` = cursor.getString(cursor.getColumnIndex(Type)),
        icon = cursor.getString(cursor.getColumnIndex(Icon)),
        themedColorIndex = cursor.getInt(cursor.getColumnIndex(ThemedColorIndex)),
        appsCategory = cursor.getString(cursor.getColumnIndex(AppsCategory)),
        constrains = cursor.getString(cursor.getColumnIndex(Constrains)),
        originalSharedCollectionId = cursor.getString(cursor.getColumnIndex(OriginalSharedCollectionId)),
        sharedCollectionId = cursor.getString(cursor.getColumnIndex(SharedCollectionId)),
        sharedCollectionSubscribed = cursor.getInt(cursor.getColumnIndex(SharedCollectionSubscribed)) > 0))
}

object CollectionEntityData {

  def collectionEntityDataFromCursor(cursor: Cursor) =
    CollectionEntityData(
      position = cursor.getInt(cursor.getColumnIndex(Position)),
      name = cursor.getString(cursor.getColumnIndex(Name)),
      `type` = cursor.getString(cursor.getColumnIndex(Type)),
      icon = cursor.getString(cursor.getColumnIndex(Icon)),
      themedColorIndex = cursor.getInt(cursor.getColumnIndex(ThemedColorIndex)),
      appsCategory = cursor.getString(cursor.getColumnIndex(AppsCategory)),
      constrains = cursor.getString(cursor.getColumnIndex(Constrains)),
      originalSharedCollectionId = cursor.getString(cursor.getColumnIndex(OriginalSharedCollectionId)),
      sharedCollectionId = cursor.getString(cursor.getColumnIndex(SharedCollectionId)),
      sharedCollectionSubscribed = cursor.getInt(cursor.getColumnIndex(SharedCollectionSubscribed)) > 0)
}