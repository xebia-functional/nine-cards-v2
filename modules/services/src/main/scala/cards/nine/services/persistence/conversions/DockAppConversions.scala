package cards.nine.services.persistence.conversions

import cards.nine.models.types.DockType
import cards.nine.models.{DockApp, DockAppData, NineCardsIntentConversions}
import cards.nine.repository.model.{
  DockApp => RepositoryDockApp,
  DockAppData => RepositoryDockAppData
}

trait DockAppConversions extends NineCardsIntentConversions {

  def toDockApp(dockApp: RepositoryDockApp): DockApp =
    DockApp(
      id = dockApp.id,
      name = dockApp.data.name,
      dockType = DockType(dockApp.data.dockType),
      intent = jsonToNineCardIntent(dockApp.data.intent),
      imagePath = dockApp.data.imagePath,
      position = dockApp.data.position)

  def toRepositoryDockApp(dockApp: DockApp): RepositoryDockApp =
    RepositoryDockApp(
      id = dockApp.id,
      data = RepositoryDockAppData(
        name = dockApp.name,
        dockType = dockApp.dockType.name,
        intent = nineCardIntentToJson(dockApp.intent),
        imagePath = dockApp.imagePath,
        position = dockApp.position))

  def toRepositoryDockApp(id: Int, dockApp: DockAppData): RepositoryDockApp =
    RepositoryDockApp(id = id, data = toRepositoryDockAppData(dockApp))

  def toRepositoryDockAppData(dockApp: DockAppData): RepositoryDockAppData =
    RepositoryDockAppData(
      name = dockApp.name,
      dockType = dockApp.dockType.name,
      intent = nineCardIntentToJson(dockApp.intent),
      imagePath = dockApp.imagePath,
      position = dockApp.position)
}
