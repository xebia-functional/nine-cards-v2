package cards.nine.models

import cards.nine.models.types.WidgetResizeMode

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
  resizeMode: WidgetResizeMode,
  updatePeriodMillis: Int,
  label: String,
  preview: Int)
