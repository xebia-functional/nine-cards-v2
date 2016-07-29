package com.fortysevendeg.ninecardslauncher.repository.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.repository.Conversions._
import com.fortysevendeg.ninecardslauncher.repository.model.Widget

case class WidgetEntity(id: Int, data: WidgetEntityData)

case class WidgetEntityData(
  momentId: Int,
  packageName: String,
  className: String,
  appWidgetId: Int,
  spanX: Int,
  spanY: Int,
  startX: Int,
  startY: Int)

object WidgetEntity {
  val table = "Widget"
  val momentId = "momentId"
  val packageName = "packageName"
  val className = "className"
  val appWidgetId = "appWidgetId"
  val spanX = "spanX"
  val spanY = "spanY"
  val startX = "startX"
  val startY = "startY"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    momentId,
    packageName,
    className,
    appWidgetId,
    spanX,
    spanY,
    startX,
    startY)

  def widgetEntityFromCursor(cursor: Cursor): WidgetEntity =
    WidgetEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = WidgetEntityData(
        momentId = cursor.getInt(cursor.getColumnIndex(momentId)),
        packageName = cursor.getString(cursor.getColumnIndex(packageName)),
        className = cursor.getString(cursor.getColumnIndex(className)),
        appWidgetId = cursor.getInt(cursor.getColumnIndex(appWidgetId)),
        spanX = cursor.getInt(cursor.getColumnIndex(spanX)),
        spanY = cursor.getInt(cursor.getColumnIndex(spanY)),
        startX = cursor.getInt(cursor.getColumnIndex(startX)),
        startY = cursor.getInt(cursor.getColumnIndex(startY))))

  def widgetFromCursor(cursor: Cursor): Widget = toWidget(widgetEntityFromCursor(cursor))

  def createTableSQL: String =
    s"""CREATE TABLE ${WidgetEntity.table}
       |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
       |${WidgetEntity.momentId} INTEGER not null,
       |${WidgetEntity.packageName} TEXT not null,
       |${WidgetEntity.className} TEXT not null,
       |${WidgetEntity.appWidgetId} INTEGER not null,
       |${WidgetEntity.spanX} INTEGER not null,
       |${WidgetEntity.spanY} INTEGER not null,
       |${WidgetEntity.startX} INTEGER not null,
       |${WidgetEntity.startY} INTEGER not null)""".stripMargin
}
