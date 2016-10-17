package cards.nine.commons.test.data

import cards.nine.commons.test.data.DockAppValues._
import cards.nine.models.{NineCardsIntentConversions, DockAppData, DockApp}
import cards.nine.models.types.DockType

trait DockAppTestData extends NineCardsIntentConversions {

  def dockApp(num: Int = 0) = DockApp(
    id = dockAppId + num,
    name = dockAppName,
    dockType = DockType(dockType),
    intent = jsonToNineCardIntent(dockAppIntent),
    imagePath = dockAppImagePath,
    position = dockAppPosition)

  val dockApp: DockApp = dockApp(0)
  val seqDockApp: Seq[DockApp]  = Seq(dockApp(0), dockApp(1), dockApp(2))

  val dockAppData: DockAppData = dockApp.toData
  val seqDockAppData: Seq[DockAppData] = seqDockApp map (_.toData)

}
