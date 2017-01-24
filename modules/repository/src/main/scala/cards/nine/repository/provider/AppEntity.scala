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
import cards.nine.repository.model.App

case class AppEntity(id: Int, data: AppEntityData)

case class AppEntityData(
    name: String,
    packageName: String,
    className: String,
    category: String,
    dateInstalled: Long,
    dateUpdate: Long,
    version: String,
    installedFromGooglePlay: Boolean)

object AppEntity {
  val table                   = "App"
  val name                    = "name"
  val packageName             = "packageName"
  val className               = "className"
  val category                = "category"
  val dateInstalled           = "dateInstalled"
  val dateUpdate              = "dateUpdate"
  val version                 = "version"
  val installedFromGooglePlay = "installedFromGooglePlay"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    name,
    packageName,
    className,
    category,
    dateInstalled,
    dateUpdate,
    version,
    installedFromGooglePlay)

  def nameFromCursor(cursor: Cursor): String =
    cursor.getString(cursor.getColumnIndex(name))

  def categoryFromCursor(cursor: Cursor): String =
    cursor.getString(cursor.getColumnIndex(category))

  def dateInstalledFromCursor(cursor: Cursor): Long =
    cursor.getLong(cursor.getColumnIndex(dateInstalled))

  def appEntityFromCursor(cursor: Cursor): AppEntity =
    AppEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = AppEntityData(
        name = cursor.getString(cursor.getColumnIndex(name)),
        packageName = cursor.getString(cursor.getColumnIndex(packageName)),
        className = cursor.getString(cursor.getColumnIndex(className)),
        category = cursor.getString(cursor.getColumnIndex(category)),
        dateInstalled = cursor.getLong(cursor.getColumnIndex(dateInstalled)),
        dateUpdate = cursor.getLong(cursor.getColumnIndex(dateUpdate)),
        version = cursor.getString(cursor.getColumnIndex(version)),
        installedFromGooglePlay = cursor
            .getInt(cursor.getColumnIndex(installedFromGooglePlay)) > 0))

  def appFromCursor(cursor: Cursor): App = toApp(appEntityFromCursor(cursor))

  def createTableSQL: String =
    s"""CREATE TABLE ${AppEntity.table}
       |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
       |${AppEntity.name} TEXT not null,
       |${AppEntity.packageName} TEXT not null,
       |${AppEntity.className} TEXT not null,
       |${AppEntity.category} TEXT not null,
       |${AppEntity.dateInstalled} INTEGER,
       |${AppEntity.dateUpdate} INTEGER,
       |${AppEntity.version} TEXT not null,
       |${AppEntity.installedFromGooglePlay} INTEGER )""".stripMargin
}
