package com.fortysevendeg.ninecardslauncher.provider

import android.database.Cursor

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

  def collectionEntityFromCursor(cursor: Cursor) = {
    CollectionEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.Id)),
      data = CollectionEntityData(
        position = cursor.getInt(cursor.getColumnIndex(CollectionEntity.Position)),
        name = cursor.getString(cursor.getColumnIndex(CollectionEntity.Name)),
        `type` = cursor.getString(cursor.getColumnIndex(CollectionEntity.Type)),
        icon = cursor.getString(cursor.getColumnIndex(CollectionEntity.Icon)),
        themedColorIndex = cursor.getInt(cursor.getColumnIndex(CollectionEntity.ThemedColorIndex)),
        appsCategory = cursor.getString(cursor.getColumnIndex(CollectionEntity.AppsCategory)),
        constrains = cursor.getString(cursor.getColumnIndex(CollectionEntity.Constrains)),
        originalSharedCollectionId = cursor.getString(cursor.getColumnIndex(CollectionEntity.OriginalSharedCollectionId)),
        sharedCollectionId = cursor.getString(cursor.getColumnIndex(CollectionEntity.SharedCollectionId)),
        sharedCollectionSubscribed = cursor.getInt(cursor.getColumnIndex(CollectionEntity.SharedCollectionSubscribed)) > 0))
  }
}

object CollectionEntityData {

  def collectionEntityDataFromCursor(cursor: Cursor) = {
    CollectionEntityData(
      position = cursor.getInt(cursor.getColumnIndex(CollectionEntity.Position)),
      name = cursor.getString(cursor.getColumnIndex(CollectionEntity.Name)),
      `type` = cursor.getString(cursor.getColumnIndex(CollectionEntity.Type)),
      icon = cursor.getString(cursor.getColumnIndex(CollectionEntity.Icon)),
      themedColorIndex = cursor.getInt(cursor.getColumnIndex(CollectionEntity.ThemedColorIndex)),
      appsCategory = cursor.getString(cursor.getColumnIndex(CollectionEntity.AppsCategory)),
      constrains = cursor.getString(cursor.getColumnIndex(CollectionEntity.Constrains)),
      originalSharedCollectionId = cursor.getString(cursor.getColumnIndex(CollectionEntity.OriginalSharedCollectionId)),
      sharedCollectionId = cursor.getString(cursor.getColumnIndex(CollectionEntity.SharedCollectionId)),
      sharedCollectionSubscribed = cursor.getInt(cursor.getColumnIndex(CollectionEntity.SharedCollectionSubscribed)) > 0)
  }
}