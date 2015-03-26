package com.fortysevendeg.ninecardslauncher.provider

import android.database.Cursor

case class CardEntity(id: Int, data: CardEntityData)

case class CardEntityData(
    position: Int,
    collectionId: Int,
    term: String,
    packageName: String,
    `type`: String,
    intent: String,
    imagePath: String,
    starRating: Double,
    micros: Int,
    numDownloads: String,
    notification: String)

object CardEntity {
  val Table = "Card"
  val Position = "position"
  val CollectionId = "collection_id"
  val Term = "term"
  val PackageName = "packageName"
  val Type = "type"
  val Intent = "intent"
  val ImagePath = "imagePath"
  val StarRating = "starRating"
  val Micros = "micros"
  val NumDownloads = "numDownloads"
  val Notification = "notification"

  val AllFields = Seq[String](
    NineCardsSqlHelper.Id,
    Position,
    CollectionId,
    Term,
    PackageName,
    Type,
    Intent,
    ImagePath,
    StarRating,
    Micros,
    NumDownloads,
    Notification)

  def cardEntityFromCursor(cursor: Cursor) = {
    CardEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.Id)),
      data = CardEntityData(
        position = cursor.getInt(cursor.getColumnIndex(CardEntity.Position)),
        collectionId = cursor.getInt(cursor.getColumnIndex(CardEntity.CollectionId)),
        term = cursor.getString(cursor.getColumnIndex(CardEntity.Term)),
        packageName = cursor.getString(cursor.getColumnIndex(CardEntity.PackageName)),
        `type` = cursor.getString(cursor.getColumnIndex(CardEntity.Type)),
        intent = cursor.getString(cursor.getColumnIndex(CardEntity.Intent)),
        imagePath = cursor.getString(cursor.getColumnIndex(CardEntity.ImagePath)),
        starRating = cursor.getInt(cursor.getColumnIndex(CardEntity.StarRating)),
        micros = cursor.getInt(cursor.getColumnIndex(CardEntity.Micros)),
        numDownloads = cursor.getString(cursor.getColumnIndex(CardEntity.NumDownloads)),
        notification = cursor.getString(cursor.getColumnIndex(CardEntity.Notification))))
  }
}

object CardEntityData {

  def cardEntityDataFromCursor(cursor: Cursor) = {
    CardEntityData(
      position = cursor.getInt(cursor.getColumnIndex(CardEntity.Position)),
      collectionId = cursor.getInt(cursor.getColumnIndex(CardEntity.CollectionId)),
      term = cursor.getString(cursor.getColumnIndex(CardEntity.Term)),
      packageName = cursor.getString(cursor.getColumnIndex(CardEntity.PackageName)),
      `type` = cursor.getString(cursor.getColumnIndex(CardEntity.Type)),
      intent = cursor.getString(cursor.getColumnIndex(CardEntity.Intent)),
      imagePath = cursor.getString(cursor.getColumnIndex(CardEntity.ImagePath)),
      starRating = cursor.getInt(cursor.getColumnIndex(CardEntity.StarRating)),
      micros = cursor.getInt(cursor.getColumnIndex(CardEntity.Micros)),
      numDownloads = cursor.getString(cursor.getColumnIndex(CardEntity.NumDownloads)),
      notification = cursor.getString(cursor.getColumnIndex(CardEntity.Notification)))
  }
}