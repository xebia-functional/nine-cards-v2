package com.fortysevendeg.ninecardslauncher.app.analytics

import Action._

sealed trait Action {
  def name: String
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

object Action {
  val openName = "Open"
  val addedToCollectionName = "AddedToCollection"
  val removedInCollectionName = "RemovedInCollection"
}