package cards.nine.models.types

sealed trait Action {
  def name: String
}

case object AddedToCollectionAction extends Action {
  override def name: String = "AddedToCollection"
}

case object AddedWidgetToMomentAction extends Action {
  override def name: String = "AddedWidgetToMoment"
}

case object OpenAction extends Action {
  override def name: String = "Open"
}

case object OpenCardAction extends Action {
  override def name: String = "OpenCard"
}

case object RemovedFromCollectionAction extends Action {
  override def name: String = "RemovedFromCollection"
}


