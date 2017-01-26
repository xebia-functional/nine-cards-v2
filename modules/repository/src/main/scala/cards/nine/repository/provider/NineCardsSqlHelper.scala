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

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import cards.nine.commons.javaNull

class NineCardsSqlHelper(context: Context)
    extends SQLiteOpenHelper(
      context,
      NineCardsSqlHelper.databaseName,
      javaNull,
      NineCardsSqlHelper.databaseVersion) {

  override def onCreate(db: SQLiteDatabase): Unit = {
    db.execSQL(AppEntity.createTableSQL)
    db.execSQL(CollectionEntity.createTableSQL)
    db.execSQL(CardEntity.createTableSQL)
    db.execSQL(DockAppEntity.createTableSQL)
    db.execSQL(MomentEntity.createTableSQL)
    db.execSQL(UserEntity.createTableSQL)
    db.execSQL(WidgetEntity.createTableSQL)
  }

  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit =
    (oldVersion + 1 to newVersion) foreach {
      case 2 =>
        db.execSQL(s"ALTER TABLE ${MomentEntity.table} ADD COLUMN ${MomentEntity.bluetooth} TEXT")
    }

}

object NineCardsSqlHelper {
  val id              = "_id"
  val databaseName    = "nine-cards.db"
  val databaseVersion = 2
}
