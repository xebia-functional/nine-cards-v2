package com.fortysevendeg.ninecardslauncher.app.ui.launcher.types

sealed trait DragLauncherType

case object ReorderCollection extends DragLauncherType

case object AddItemToCollection extends DragLauncherType

case object UnknownLauncherType extends DragLauncherType

object DragLauncherType {

  def apply(data: AnyRef): DragLauncherType = data match {
    case ReorderCollection => ReorderCollection
    case AddItemToCollection => AddItemToCollection
    case _ => UnknownLauncherType
  }

}