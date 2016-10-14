package cards.nine.services.persistence.data

import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.test.data.DockAppValues._
import cards.nine.repository.model.{DockApp => RepositoryDockApp, DockAppData => RepositoryDockAppData}
import cards.nine.services.persistence.models.IterableDockApps

trait DockAppPersistenceServicesData{

  def createSeqRepoDockApp(
    num: Int = 5,
    id: Int = dockAppId,
    data: RepositoryDockAppData = createRepoDockAppData()): Seq[RepositoryDockApp] =
    List.tabulate(num)(item => RepositoryDockApp(id = id + item, data = data))

  def createRepoDockAppData(
    name: String = name,
    dockType: String = dockType,
    intent: String = intent,
    imagePath: String = imagePath,
    position: Int = position): RepositoryDockAppData =
    RepositoryDockAppData(
      name = name,
      dockType = dockType,
      intent = intent,
      imagePath = imagePath,
      position = position)

  val repoDockAppData: RepositoryDockAppData = createRepoDockAppData()
  val seqRepoDockApp: Seq[RepositoryDockApp] = createSeqRepoDockApp(data = repoDockAppData)
  val repoDockApp: RepositoryDockApp = seqRepoDockApp(0)

  val iterableCursorDockApps = new IterableCursor[RepositoryDockApp] {
    override def count(): Int = seqRepoDockApp.length
    override def moveToPosition(pos: Int): RepositoryDockApp = seqRepoDockApp(pos)
    override def close(): Unit = ()
  }
  val iterableDockApps = new IterableDockApps(iterableCursorDockApps)

}
