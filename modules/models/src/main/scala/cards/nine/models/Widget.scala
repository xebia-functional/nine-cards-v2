package cards.nine.models

import cards.nine.models.types.{WidgetResizeMode, WidgetType}

case class AppsWithWidgets(
  packageName: String,
  name: String,
  widgets: Seq[AppWidget])

case class AppWidget (
  userHashCode: Option[Int],
  autoAdvanceViewId: Int,
  initialLayout: Int,
  minHeight: Int,
  minResizeHeight: Int,
  minResizeWidth: Int,
  minWidth: Int,
  className: String,
  packageName: String,
  resizeMode: WidgetResizeMode,
  updatePeriodMillis: Int,
  label: String,
  preview: Int)

case class Widget(
  id: Int,
  momentId: Int,
  packageName: String,
  className: String,
  appWidgetId: Option[Int],
  area: WidgetArea,
  widgetType: WidgetType,
  label: Option[String],
  imagePath: Option[String],
  intent: Option[NineCardsIntent])

case class WidgetData(
  momentId: Int = 0,
  packageName: String,
  className: String,
  appWidgetId: Option[Int],
  area: WidgetArea,
  widgetType: WidgetType,
  label: Option[String],
  imagePath: Option[String],
  intent: Option[NineCardsIntent])

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

object Widget {

  implicit class WidgetOps(widget: Widget) {

    def toData = WidgetData(
      momentId = widget.momentId,
      packageName = widget.packageName,
      className = widget.className,
      appWidgetId = widget.appWidgetId,
      area = widget.area,
      widgetType = widget.widgetType,
      label = widget.label,
      imagePath = widget.imagePath,
      intent = widget.intent)

  }
}