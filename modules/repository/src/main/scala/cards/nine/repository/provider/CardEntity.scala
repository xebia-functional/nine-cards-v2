/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.repository.provider

import android.database.Cursor
import cards.nine.repository.Conversions._
import cards.nine.repository.model.Card

case class CardEntity(id: Int, data: CardEntityData)

case class CardEntityData(
    position: Int,
    collectionId: Int,
    term: String,
    packageName: String,
    `type`: String,
    intent: String,
    imagePath: String,
    notification: String)

object CardEntity {
  val table        = "Card"
  val position     = "position"
  val collectionId = "collection_id"
  val term         = "term"
  val packageName  = "packageName"
  val cardType     = "type"
  val intent       = "intent"
  val imagePath    = "imagePath"
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
    notification)

  def cardEntityFromCursor(cursor: Cursor): CardEntity =
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
        notification = cursor.getString(cursor.getColumnIndex(notification))))

  def cardFromCursor(cursor: Cursor): Card = toCard(cardEntityFromCursor(cursor))

  def createTableSQL: String =
    s"""CREATE TABLE ${CardEntity.table}
       |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
       |${CardEntity.position} INTEGER not null,
       |${CardEntity.collectionId} INTEGER not null,
       |${CardEntity.term} TEXT not null,
       |${CardEntity.packageName} TEXT,
       |${CardEntity.cardType} TEXT not null,
       |${CardEntity.intent} TEXT,
       |${CardEntity.imagePath} TEXT,
       |${CardEntity.notification} TEXT)""".stripMargin
}
