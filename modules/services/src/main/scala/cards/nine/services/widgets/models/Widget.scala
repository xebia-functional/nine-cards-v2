package cards.nine.services.widgets.models

case class Widget (
  userHashCode: Option[Int],
  autoAdvanceViewId: Int,
  initialLayout: Int,
  minHeight: Int,
  minResizeHeight: Int,
  minResizeWidth: Int,
  minWidth: Int,
  className: String,
  packageName: String,
  resizeMode: Int,
  updatePeriodMillis: Int,
  label: String,
  preview: Int)



