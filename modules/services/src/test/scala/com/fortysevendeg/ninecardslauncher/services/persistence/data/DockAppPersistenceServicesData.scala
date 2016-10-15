package cards.nine.services.persistence.data

import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.test.data.DockAppValues._
import cards.nine.repository.model.{DockApp, DockAppData}
import cards.nine.services.persistence.models.IterableDockApps

trait DockAppPersistenceServicesData{


  def dockAppData(num: Int = 0) = DockAppData(
    name = dockAppName,
    dockType = dockType,
    intent = dockAppIntent,
    imagePath = dockAppImagePath,
    position = dockAppPosition)

  val repoDockAppData: DockAppData = dockAppData(0)
  val seqRepoDockAppData: Seq[DockAppData] = Seq(dockAppData(0), dockAppData(1), dockAppData(2))

  def dockApp(num: Int = 0) = DockApp(
    id = dockAppId + item,
    data = dockAppData(num))

  val repoDockApp: DockApp = dockApp(0)
  val seqRepoDockApp: Seq[DockApp]  = Seq(dockApp(0), dockApp(1), dockApp(2))

  val iterableCursorDockApps = new IterableCursor[DockApp] {
    override def count(): Int = seqRepoDockApp.length
    override def moveToPosition(pos: Int): DockApp = seqRepoDockApp(pos)
    override def close(): Unit = ()
  }
  val iterableDockApps = new IterableDockApps(iterableCursorDockApps)

}
