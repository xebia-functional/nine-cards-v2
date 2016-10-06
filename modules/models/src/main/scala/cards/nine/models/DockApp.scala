package cards.nine.models

case class DockApp(
  id: Int,
  name: String,
  dockType: String,
  intent: String,
  imagePath: String,
  position: Int)

case class DockAppData(
  name: String,
  dockType: String,
  intent: String,
  imagePath: String,
  position: Int)

object DockApp {

  implicit class DockAppOps(dockApp: DockApp) {

    def toData = DockAppData(
      name = dockApp.name,
      dockType = dockApp.dockType,
      intent = dockApp.intent,
      imagePath = dockApp.imagePath,
      position = dockApp.position)

  }
}

