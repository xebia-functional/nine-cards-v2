package cards.nine.services.persistence.conversions

import cards.nine.models.{DockAppData, DockApp}
import cards.nine.repository.model.{DockApp => RepositoryDockApp, DockAppData => RepositoryDockAppData}

trait DockAppConversions {

  def toDockApp(dockApp: RepositoryDockApp): DockApp =
    DockApp(
      id = dockApp.id,
      name = dockApp.data.name,
      dockType = dockApp.data.dockType,
      intent = dockApp.data.intent,
      imagePath = dockApp.data.imagePath,
      position = dockApp.data.position)

  def toRepositoryDockApp(dockApp: DockApp): RepositoryDockApp =
    RepositoryDockApp(
      id = dockApp.id,
      data = RepositoryDockAppData(
        name = dockApp.name,
        dockType = dockApp.dockType,
        intent = dockApp.intent,
        imagePath = dockApp.imagePath,
        position = dockApp.position))

  def toRepositoryDockApp(id: Int, dockApp: DockAppData): RepositoryDockApp =
    RepositoryDockApp(
      id = id,
      data = toRepositoryDockAppData(dockApp))

  def toRepositoryDockAppData(dockApp: DockAppData): RepositoryDockAppData =
    RepositoryDockAppData(
      name = dockApp.name,
      dockType = dockApp.dockType,
      intent = dockApp.intent,
      imagePath = dockApp.imagePath,
      position = dockApp.position)
}
