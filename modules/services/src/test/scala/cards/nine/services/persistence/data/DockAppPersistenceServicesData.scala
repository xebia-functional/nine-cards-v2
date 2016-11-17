package cards.nine.services.persistence.data

import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.DockAppValues._
import cards.nine.models.IterableCursor
import cards.nine.repository.model.{DockApp, DockAppData}

trait DockAppPersistenceServicesData{
  
  def repoDockAppData(num: Int = 0) = DockAppData(
    name = dockAppName,
    dockType = dockType,
    intent = intent,
    imagePath = dockAppImagePath,
    position = dockAppPosition + num)

  val repoDockAppData: DockAppData = repoDockAppData(0)
  val seqRepoDockAppData: Seq[DockAppData] = Seq(repoDockAppData(0), repoDockAppData(1), repoDockAppData(2))

  def repoDockApp(num: Int = 0) = DockApp(
    id = dockAppId + num,
    data = repoDockAppData(num))

  val repoDockApp: DockApp = repoDockApp(0)
  val seqRepoDockApp: Seq[DockApp]  = Seq(repoDockApp(0), repoDockApp(1), repoDockApp(2))

  val iterableCursorDockApps = new IterableCursor[DockApp] {
    override def count(): Int = seqRepoDockApp.length
    override def moveToPosition(pos: Int): DockApp = seqRepoDockApp(pos)
    override def close(): Unit = ()
  }

}
