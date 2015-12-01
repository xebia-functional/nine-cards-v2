package com.fortysevendeg.ninecardslauncher.repository.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.repository.Conversions._

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
  val table = "Card"
  val position = "position"
  val collectionId = "collection_id"
  val term = "term"
  val packageName = "packageName"
  val cardType = "type"
  val intent = "intent"
  val imagePath = "imagePath"
  val starRating = "starRating"
  val micros = "micros"
  val numDownloads = "numDownloads"
  val notification = "notification"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    position,
    collectionId,
    term,
    packageName,
    cardType,
    intent,
    imagePath,
    starRating,
    micros,
    numDownloads,
    notification)

  def cardEntityFromCursor(cursor: Cursor) =
    CardEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = CardEntityData(
        position = cursor.getInt(cursor.getColumnIndex(position)),
        collectionId = cursor.getInt(cursor.getColumnIndex(collectionId)),
        term = cursor.getString(cursor.getColumnIndex(term)),
        packageName = cursor.getString(cursor.getColumnIndex(packageName)),
        `type` = cursor.getString(cursor.getColumnIndex(cardType)),
        intent = cursor.getString(cursor.getColumnIndex(intent)),
        imagePath = cursor.getString(cursor.getColumnIndex(imagePath)),
        starRating = cursor.getDouble(cursor.getColumnIndex(starRating)),
        micros = cursor.getInt(cursor.getColumnIndex(micros)),
        numDownloads = cursor.getString(cursor.getColumnIndex(numDownloads)),
        notification = cursor.getString(cursor.getColumnIndex(notification))))

  def cardFromCursor(cursor: Cursor) = toCard(cardEntityFromCursor(cursor))

  def createTableSQL =
    s"""CREATE TABLE ${CardEntity.table}
       |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
       |${CardEntity.position} INTEGER not null,
       |${CardEntity.collectionId} INTEGER not null,
       |${CardEntity.term} TEXT not null,
       |${CardEntity.packageName} TEXT,
       |${CardEntity.cardType} TEXT not null,
       |${CardEntity.intent} TEXT,
       |${CardEntity.imagePath} TEXT,
       |${CardEntity.starRating} DOUBLE,
       |${CardEntity.micros} INTEGER,
       |${CardEntity.notification} TEXT,
       |${CardEntity.numDownloads} TEXT )""".stripMargin
}
