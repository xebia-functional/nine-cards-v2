package com.fortysevendeg.ninecardslauncher.app.ui.launcher.types

import DragLauncherNames._

sealed trait DragLauncherType {
  val name: String
}

case object ReorderCollection extends DragLauncherType {
  override val name: String = reorder
}

case object AddItemToCollection extends DragLauncherType {
  override val name: String = addItem
}

case object UnknownLauncherType extends DragLauncherType {
  override val name: String = unknown
}

object DragLauncherType {

  def apply(name: String): DragLauncherType = name match {
    case `reorder` => ReorderCollection
    case `addItem` => AddItemToCollection
    case `unknown` => UnknownLauncherType
  }

}

object DragLauncherNames {
  val reorder = "reorder"
  val addItem = "add-item"
  val unknown = "unknown"
}