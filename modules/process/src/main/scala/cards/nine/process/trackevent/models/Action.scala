package cards.nine.process.trackevent.models

import Action._

sealed trait Action {
  def name: String
}

case object OpenCardAction extends Action {
  override def name: String = openCardName
}

case object OpenAction extends Action {
  override def name: String = openName
}

case object AddedToCollectionAction extends Action {
  override def name: String = addedToCollectionName
}

case object RemovedFromCollectionAction extends Action {
  override def name: String = removedFromCollectionName
}

case object AddedWidgetToMomentAction extends Action {
  override def name: String = addedWidgetToMomentName
}

object Action {
  val openName = "Open"
  val openCardName = "OpenCard"
  val addedToCollectionName = "AddedToCollection"
  val removedFromCollectionName = "RemovedFromCollection"
  val addedWidgetToMomentName = "AddedWidgetToMoment"
}