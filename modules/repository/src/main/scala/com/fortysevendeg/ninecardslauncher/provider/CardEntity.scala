package com.fortysevendeg.ninecardslauncher.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.provider.CardEntity._

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

  val AllFields = Array[String](
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
        position = cursor.getInt(cursor.getColumnIndex(Position)),
        collectionId = cursor.getInt(cursor.getColumnIndex(CollectionId)),
        term = cursor.getString(cursor.getColumnIndex(Term)),
        packageName = cursor.getString(cursor.getColumnIndex(PackageName)),
        `type` = cursor.getString(cursor.getColumnIndex(Type)),
        intent = cursor.getString(cursor.getColumnIndex(Intent)),
        imagePath = cursor.getString(cursor.getColumnIndex(ImagePath)),
        starRating = cursor.getInt(cursor.getColumnIndex(StarRating)),
        micros = cursor.getInt(cursor.getColumnIndex(Micros)),
        numDownloads = cursor.getString(cursor.getColumnIndex(NumDownloads)),
        notification = cursor.getString(cursor.getColumnIndex(Notification))))
  }
}

object CardEntityData {

  def cardEntityDataFromCursor(cursor: Cursor) = {
    CardEntityData(
      position = cursor.getInt(cursor.getColumnIndex(Position)),
      collectionId = cursor.getInt(cursor.getColumnIndex(CollectionId)),
      term = cursor.getString(cursor.getColumnIndex(Term)),
      packageName = cursor.getString(cursor.getColumnIndex(PackageName)),
      `type` = cursor.getString(cursor.getColumnIndex(Type)),
      intent = cursor.getString(cursor.getColumnIndex(Intent)),
      imagePath = cursor.getString(cursor.getColumnIndex(ImagePath)),
      starRating = cursor.getInt(cursor.getColumnIndex(StarRating)),
      micros = cursor.getInt(cursor.getColumnIndex(Micros)),
      numDownloads = cursor.getString(cursor.getColumnIndex(NumDownloads)),
      notification = cursor.getString(cursor.getColumnIndex(Notification)))
  }
}