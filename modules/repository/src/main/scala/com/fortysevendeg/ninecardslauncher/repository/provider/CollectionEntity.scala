package com.fortysevendeg.ninecardslauncher.repository.provider

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
  val table = "Collection"
  val position = "position"
  val name = "name"
  val collectionType = "type"
  val icon = "icon"
  val themedColorIndex = "themedColorIndex"
  val appsCategory = "appsCategory"
  val constrains = "constrains"
  val originalSharedCollectionId = "originalSharedCollectionId"
  val sharedCollectionId = "sharedCollectionId"
  val sharedCollectionSubscribed = "sharedCollectionSubscribed"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    position,
    name,
    collectionType,
    icon,
    themedColorIndex,
    appsCategory,
    constrains,
    originalSharedCollectionId,
    sharedCollectionId,
    sharedCollectionSubscribed)

  def collectionEntityFromCursor(cursor: Cursor) =
    CollectionEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = CollectionEntityData(
        position = cursor.getInt(cursor.getColumnIndex(position)),
        name = cursor.getString(cursor.getColumnIndex(name)),
        `type` = cursor.getString(cursor.getColumnIndex(collectionType)),
        icon = cursor.getString(cursor.getColumnIndex(icon)),
        themedColorIndex = cursor.getInt(cursor.getColumnIndex(themedColorIndex)),
        appsCategory = cursor.getString(cursor.getColumnIndex(appsCategory)),
        constrains = cursor.getString(cursor.getColumnIndex(constrains)),
        originalSharedCollectionId = cursor.getString(cursor.getColumnIndex(originalSharedCollectionId)),
        sharedCollectionId = cursor.getString(cursor.getColumnIndex(sharedCollectionId)),
        sharedCollectionSubscribed = cursor.getInt(cursor.getColumnIndex(sharedCollectionSubscribed)) > 0))

  def createTableSQL =
    s"""CREATE TABLE ${CollectionEntity.table}
       |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
       |${CollectionEntity.position} INTEGER not null,
       |${CollectionEntity.name} TEXT not null,
       |${CollectionEntity.collectionType} TEXT not null,
       |${CollectionEntity.icon} TEXT not null,
       |${CollectionEntity.themedColorIndex} INTEGER not null,
       |${CollectionEntity.appsCategory} TEXT,
       |${CollectionEntity.originalSharedCollectionId} TEXT,
       |${CollectionEntity.sharedCollectionId} TEXT,
       |${CollectionEntity.sharedCollectionSubscribed} INTEGER,
       |${CollectionEntity.constrains} TEXT )""".stripMargin
}
