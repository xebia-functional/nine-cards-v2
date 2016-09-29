package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.repository.model.{DockApp => RepositoryDockApp, DockAppData => RepositoryDockAppData}
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindDockAppByIdRequest, DeleteDockAppRequest, CreateOrUpdateDockAppRequest}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{IterableDockApps, DockApp}

import scala.util.Random

trait DockAppPersistenceServicesData extends PersistenceServicesData {

  val dockAppId: Int = Random.nextInt(10)
  val nonExistentDockAppId: Int = Random.nextInt(10) + 100
  val dockType: String = Random.nextString(5)

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
        dockType = dockType,
        intent = intent,
        imagePath = imagePath,
        position = position))

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

  val seqDockApp: Seq[DockApp] = createSeqDockApp()
  val dockApp: DockApp = seqDockApp(0)
  val repoDockAppData: RepositoryDockAppData = createRepoDockAppData()
  val seqRepoDockApp: Seq[RepositoryDockApp] = createSeqRepoDockApp(data = repoDockAppData)
  val repoDockApp: RepositoryDockApp = seqRepoDockApp(0)

  def createCreateOrUpdateDockAppRequest(
    name: String = name,
    dockType: String = dockType,
    intent: String = intent,
    imagePath: String = imagePath,
    position: Int = position): CreateOrUpdateDockAppRequest =
    CreateOrUpdateDockAppRequest(
      name = name,
      dockType = dockType,
      intent = intent,
      imagePath = imagePath,
      position = position)

  def createDeleteDockAppRequest(dockApp: DockApp): DeleteDockAppRequest =
    DeleteDockAppRequest(dockApp = dockApp)

  def createFindDockAppByIdRequest(id: Int): FindDockAppByIdRequest =
    FindDockAppByIdRequest(id = id)

  val iterableCursorDockApps = new IterableCursor[RepositoryDockApp] {
    override def count(): Int = seqRepoDockApp.length
    override def moveToPosition(pos: Int): RepositoryDockApp = seqRepoDockApp(pos)
    override def close(): Unit = ()
  }
  val iterableDockApps = new IterableDockApps(iterableCursorDockApps)

}
