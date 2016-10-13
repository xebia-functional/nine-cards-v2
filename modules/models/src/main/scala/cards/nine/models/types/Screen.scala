package cards.nine.models.types

import cards.nine.models.types.Screen._

sealed trait Screen {
  def name: String
}

case object WizardScreen extends Screen {
  override def name: String = wizardName
}

case object LauncherScreen extends Screen {
  override def name: String = launcherName
}

case object CollectionDetailScreen extends Screen {
  override def name: String = collectionDetailName
}
case object WidgetScreen extends Screen {
  override def name: String = widgetName
}

object Screen {
  val wizardName = "Wizard"
  val widgetName = "Wizard"
  val launcherName = "Launcher"
  val collectionDetailName = "CollectionDetail"
}