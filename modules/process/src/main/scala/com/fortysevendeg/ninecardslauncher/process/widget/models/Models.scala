package com.fortysevendeg.ninecardslauncher.process.widget.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.WidgetType

case class AppWidget(
  id: Int,
  momentId: Int,
  packageName: String,
  className: String,
  appWidgetId: Int,
  startX: Int,
  startY: Int,
  spanX: Int,
  spanY: Int,
  widgetType: WidgetType,
  label: Option[String],
  imagePath: Option[String],
  intent: Option[String])