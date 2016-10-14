package cards.nine.commons.test.data

import cards.nine.commons.test.data.DockAppValues._
import cards.nine.models.{NineCardsIntentConversions, DockAppData, DockApp}
import cards.nine.models.types.DockType

trait DockAppTestData extends NineCardsIntentConversions {

  def createSeqDockApp(
    num: Int = 5,
    id: Int = dockAppId,
    name: String = name,
    dockType: String = dockType,
    intent: String = intent,
    imagePath: String = imagePath,
    position: Int = position): Seq[DockApp] = List.tabulate(num)(
    item =>
      DockApp(
        id = id + item,
        name = name,
        dockType = DockType(dockType),
        intent = jsonToNineCardIntent(intent),
        imagePath = imagePath,
        position = position))

  def createCreateOrUpdateDockAppRequest(
    name: String = name,
    dockType: String = dockType,
    intent: String = intent,
    imagePath: String = imagePath,
    position: Int = position): DockAppData =
    DockAppData(
      name = name,
      dockType = DockType(dockType),
      intent = jsonToNineCardIntent(intent),
      imagePath = imagePath,
      position = position)

  val seqDockApp: Seq[DockApp] = createSeqDockApp()
  val dockApp: DockApp = seqDockApp(0)

}
