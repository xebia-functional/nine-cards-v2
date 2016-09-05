package com.fortysevendeg.ninecardslauncher.process.widget.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.WidgetType

case class AppWidget(
  id: Int,
  momentId: Int,
  packageName: String,
  className: String,
  appWidgetId: Option[Int],
  area: WidgetArea,
  widgetType: WidgetType,
  label: Option[String],
  imagePath: Option[String],
  intent: Option[String])

case class WidgetArea (
  startX: Int,
  startY: Int,
  spanX: Int,
  spanY: Int) {

  def intersect(other: WidgetArea, limits: Option[(Int, Int)] = None): Boolean = {
    def valueInRange(value: Int, min: Int, max: Int) = (value >= min) && (value < max)

    val xOverlap = valueInRange(startX, other.startX, other.startX + other.spanX) ||
      valueInRange(other.startX, startX, startX + spanX)

    val yOverlap = valueInRange(startY, other.startY, other.startY + other.spanY) ||
      valueInRange(other.startY, startY, startY + spanY)

    val outOfLimits = limits exists {
      case (x, y) => (startX < 0) || (startY < 0) || (startX + spanX > x) || (startY + spanY > y)
    }

    (xOverlap && yOverlap) || outOfLimits
  }

}