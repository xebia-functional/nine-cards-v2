package cards.nine.models.types

sealed trait Screen {
  def name: String
}

case object CollectionDetailScreen extends Screen {
  override def name: String = "CollectionDetail"
}

case object LauncherScreen extends Screen {
  override def name: String = "Launcher"
}

case object ProfileScreen extends Screen {
  override def name: String = "Profile"
}

case object WidgetScreen extends Screen {
  override def name: String = "Widget"
}

case object WizardScreen extends Screen {
  override def name: String = "Wizard"
}
