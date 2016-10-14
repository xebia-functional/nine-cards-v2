package cards.nine.models

import cards.nine.models.types.{Value, Action, Screen, Category}

case class TrackEvent(
  screen: Screen,
  category: Category,
  action: Action,
  label: Option[String] = None,
  value: Option[Value] = None)
