package cards.nine.commons.test.data

import cards.nine.commons.test.data.DockAppValues._
import cards.nine.models.{NineCardsIntentConversions, DockAppData, DockApp}
import cards.nine.models.types.DockType

trait DockAppTestData extends NineCardsIntentConversions {

  def dockApp(num: Int = 0) = DockApp(
    id = dockAppId + item,
    name = dockAppName,
    dockType = DockType(dockType),
    intent = jsonToNineCardIntent(dockAppIntent),
    imagePath = dockAppImagePath,
    position = dockAppPosition)

  val dockApp: DockApp = dockApp(0)
  val seqDockApp: Seq[DockApp]  = Seq(dockApp(0), dockApp(1), dockApp(2))

  def dockAppData(num: Int = 0) = DockAppData(
    name = dockAppName,
    dockType = DockType(dockType),
    intent = jsonToNineCardIntent(dockAppIntent),
    imagePath = dockAppImagePath,
    position = dockAppPosition)

  val dockAppData: DockAppData = dockAppData(0)
  val seqDockAppData: Seq[DockAppData] = Seq(dockAppData(0), dockAppData(1), dockAppData(2))

}
