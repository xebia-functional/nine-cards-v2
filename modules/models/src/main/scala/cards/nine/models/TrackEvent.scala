package cards.nine.models

import cards.nine.models.types.{Action, Category, Screen, Value}

case class TrackEvent(
    screen: Screen,
    category: Category,
    action: Action,
    label: Option[String] = None,
    value: Option[Value] = None)
