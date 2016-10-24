package cards.nine.app.ui.launcher.types

sealed trait DragLauncherType {
  val name: String
}

case object ReorderCollection extends DragLauncherType {
  override val name: String = "reorder"
}

case object AddItemToCollection extends DragLauncherType {
  override val name: String = "add-item"
}

case object UnknownLauncherType extends DragLauncherType {
  override val name: String = "unknown"
}
