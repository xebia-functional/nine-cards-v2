package com.fortysevendeg.ninecardslauncher.process.trackevent

import com.fortysevendeg.ninecardslauncher.process.trackevent.Action._

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

case object RemovedInCollectionAction extends Action {
  override def name: String = removedInCollectionName
}

case object AddedWidgetToMomentAction extends Action {
  override def name: String = addedWidgetToMomentName
}

object Action {
  val openName = "Open"
  val openCardName = "OpenCard"
  val addedToCollectionName = "AddedToCollection"
  val removedInCollectionName = "RemovedInCollection"
  val addedWidgetToMomentName = "AddedWidgetToMoment"
}