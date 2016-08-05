package com.fortysevendeg.ninecardslauncher.process.widget

import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppWidgetType, WidgetType}

case class AddWidgetRequest(
  momentId: Int,
  packageName: String,
  className: String,
  appWidgetId: Int,
  startX: Int,
  startY: Int,
  spanX: Int,
  spanY: Int,
  widgetType: WidgetType = AppWidgetType,
  label: Option[String] = None,
  imagePath: Option[String] = None,
  intent: Option[String] = None)

case class MoveWidgetRequest(
  startX: Int,
  startY: Int)

case class ResizeWidgetRequest(
  spanX: Int,
  spanY: Int)