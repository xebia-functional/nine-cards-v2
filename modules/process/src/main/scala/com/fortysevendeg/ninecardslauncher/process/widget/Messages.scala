package cards.nine.process.widget

import cards.nine.process.commons.types.{AppWidgetType, WidgetType}

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
  displaceX: Int,
  displaceY: Int)

case class ResizeWidgetRequest(
  increaseX: Int,
  increaseY: Int)