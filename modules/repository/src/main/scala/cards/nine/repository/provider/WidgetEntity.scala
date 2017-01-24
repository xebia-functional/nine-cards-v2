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
import cards.nine.repository.model.Widget

case class WidgetEntity(id: Int, data: WidgetEntityData)

case class WidgetEntityData(
    momentId: Int,
    packageName: String,
    className: String,
    appWidgetId: Int,
    startX: Int,
    startY: Int,
    spanX: Int,
    spanY: Int,
    widgetType: String,
    label: String,
    imagePath: String,
    intent: String)

object WidgetEntity {
  val table       = "Widget"
  val momentId    = "momentId"
  val packageName = "packageName"
  val className   = "className"
  val appWidgetId = "appWidgetId"
  val startX      = "startX"
  val startY      = "startY"
  val spanX       = "spanX"
  val spanY       = "spanY"
  val widgetType  = "widgetType"
  val label       = "label"
  val imagePath   = "imagePath"
  val intent      = "intent"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    momentId,
    packageName,
    className,
    appWidgetId,
    startX,
    startY,
    spanX,
    spanY,
    widgetType,
    label,
    imagePath,
    intent)

  def widgetEntityFromCursor(cursor: Cursor): WidgetEntity =
    WidgetEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = WidgetEntityData(
        momentId = cursor.getInt(cursor.getColumnIndex(momentId)),
        packageName = cursor.getString(cursor.getColumnIndex(packageName)),
        className = cursor.getString(cursor.getColumnIndex(className)),
        appWidgetId = cursor.getInt(cursor.getColumnIndex(appWidgetId)),
        startX = cursor.getInt(cursor.getColumnIndex(startX)),
        startY = cursor.getInt(cursor.getColumnIndex(startY)),
        spanX = cursor.getInt(cursor.getColumnIndex(spanX)),
        spanY = cursor.getInt(cursor.getColumnIndex(spanY)),
        widgetType = cursor.getString(cursor.getColumnIndex(widgetType)),
        label = cursor.getString(cursor.getColumnIndex(label)),
        imagePath = cursor.getString(cursor.getColumnIndex(imagePath)),
        intent = cursor.getString(cursor.getColumnIndex(intent))))

  def widgetFromCursor(cursor: Cursor): Widget = toWidget(widgetEntityFromCursor(cursor))

  def createTableSQL: String =
    s"""CREATE TABLE ${WidgetEntity.table}
        |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
        |${WidgetEntity.momentId} INTEGER not null,
        |${WidgetEntity.packageName} TEXT not null,
        |${WidgetEntity.className} TEXT not null,
        |${WidgetEntity.appWidgetId} INTEGER not null,
        |${WidgetEntity.startX} INTEGER not null,
        |${WidgetEntity.startY} INTEGER not null,
        |${WidgetEntity.spanX} INTEGER not null,
        |${WidgetEntity.spanY} INTEGER not null,
        |${WidgetEntity.widgetType} TEXT not null,
        |${WidgetEntity.label} TEXT,
        |${WidgetEntity.imagePath} TEXT,
        |${WidgetEntity.intent} TEXT)""".stripMargin
}
