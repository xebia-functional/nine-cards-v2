package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import cards.nine.repository.model.{DockApp => RepositoryDockApp, DockAppData => RepositoryDockAppData}
import com.fortysevendeg.ninecardslauncher.services.persistence.CreateOrUpdateDockAppRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.DockApp

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

  def toRepositoryDockApp(id: Int, request: CreateOrUpdateDockAppRequest): RepositoryDockApp =
    RepositoryDockApp(
      id = id,
      data = toRepositoryDockAppData(request))

  def toRepositoryDockAppData(request: CreateOrUpdateDockAppRequest): RepositoryDockAppData =
    RepositoryDockAppData(
      name = request.name,
      dockType = request.dockType,
      intent = request.intent,
      imagePath = request.imagePath,
      position = request.position)
}
