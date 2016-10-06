package cards.nine.models

case class PersistenceDockApp(
  id: Int,
  name: String,
  dockType: String,
  intent: String,
  imagePath: String,
  position: Int)

case class DataCounter(term: String, count: Int)

case class PersistenceWidget(
  id: Int,
  momentId: Int,
  packageName: String,
  className: String,
  appWidgetId: Option[Int],
  startX: Int,
  startY: Int,
  spanX: Int,
  spanY: Int,
  widgetType: String,
  label: Option[String],
  imagePath: Option[String],
  intent: Option[String])