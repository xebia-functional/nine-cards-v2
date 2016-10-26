package cards.nine.commons.test.data

import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.DockAppValues._
import cards.nine.models.types.DockType
import cards.nine.models.{DockApp, DockAppData, NineCardsIntentConversions}

trait DockAppTestData extends NineCardsIntentConversions {

  def dockApp(num: Int = 0) = DockApp(
    id = dockAppId + num,
    name = dockAppName,
    dockType = DockType(dockType),
    intent = jsonToNineCardIntent(intent),
    imagePath = dockAppImagePath,
    position = dockAppPosition)

  val dockApp: DockApp = dockApp(0)
  val seqDockApp: Seq[DockApp]  = Seq(dockApp(0), dockApp(1), dockApp(2))

  val dockAppData: DockAppData = dockApp.toData
  val seqDockAppData: Seq[DockAppData] = seqDockApp map (_.toData)

}
