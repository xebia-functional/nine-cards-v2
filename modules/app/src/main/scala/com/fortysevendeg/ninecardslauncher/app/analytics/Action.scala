package com.fortysevendeg.ninecardslauncher.app.analytics

import Action._

sealed trait Action {
  def name: String
}

case class OpenAction() extends Action {
  override def name: String = openName
}
case class AddedToCollectionAction() extends Action {
  override def name: String = addedToCollectionName
}

case class RemovedInCollectionAction() extends Action {
  override def name: String = removedInCollectionName
}

object Action {
  val openName = "Open"
  val addedToCollectionName = "AddedToCollection"
  val removedInCollectionName = "RemovedInCollection"
}