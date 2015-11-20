package com.fortysevendeg.ninecardslauncher.repository.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.repository.provider.DockAppEntity._

case class DockAppEntity(id: Int, data: DockAppEntityData)

case class DockAppEntityData(
  name: String,
  cardType: String,
  intent: String,
  imagePath: String,
  position: Int)

object DockAppEntity {
  val table = "DockApp"
  val name = "name"
  val cardType = "cardType"
  val intent = "intent"
  val imagePath = "imagePath"
  val position = "position"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    name,
    cardType,
    intent,
    imagePath,
    position)

  def dockAppEntityFromCursor(cursor: Cursor) =
    DockAppEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = DockAppEntityData(
        name = cursor.getString(cursor.getColumnIndex(name)),
        cardType = cursor.getString(cursor.getColumnIndex(cardType)),
        intent = cursor.getString(cursor.getColumnIndex(intent)),
        imagePath = cursor.getString(cursor.getColumnIndex(imagePath)),
        position = cursor.getInt(cursor.getColumnIndex(position))))

  def createTableSQL =
    s"""CREATE TABLE ${DockAppEntity.table}
        |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
        |${DockAppEntity.name} TEXT not null,
        |${DockAppEntity.cardType} TEXT not null,
        |${DockAppEntity.intent} TEXT not null,
        |${DockAppEntity.imagePath} TEXT not null,
        |${DockAppEntity.position} INTEGER not null)""".stripMargin
}

object DockAppEntityData {

  def dockAppEntityDataFromCursor(cursor: Cursor) =
    DockAppEntityData(
      name = cursor.getString(cursor.getColumnIndex(name)),
      cardType = cursor.getString(cursor.getColumnIndex(cardType)),
      intent = cursor.getString(cursor.getColumnIndex(intent)),
      imagePath = cursor.getString(cursor.getColumnIndex(imagePath)),
      position = cursor.getInt(cursor.getColumnIndex(position)))
}