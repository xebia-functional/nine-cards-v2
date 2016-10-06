package cards.nine.models

case class TrackEvent(
  screen: String,
  category: String,
  action: String,
  label: Option[String] = None,
  value: Option[Long] = None)
