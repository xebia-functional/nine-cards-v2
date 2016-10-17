package cards.nine.models

import cards.nine.models.types.DockType

case class DockApp(
  id: Int,
  name: String,
  dockType: DockType,
  intent: NineCardsIntent,
  imagePath: String,
  position: Int)

case class DockAppData(
  name: String,
  dockType: DockType,
  intent: NineCardsIntent,
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

