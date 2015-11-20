package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{DockApp => RepoDockApp, DockAppData => RepoDockAppData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.DockApp

trait DockAppConversions {

  def toDockAppSeq(dockApp: Seq[RepoDockApp]): Seq[DockApp] = dockApp map toDockApp

  def toDockApp(dockApp: RepoDockApp): DockApp =
    DockApp(
      id = dockApp.id,
      name = dockApp.data.name,
      cardType = dockApp.data.cardType,
      collectionId = dockApp.data.collectionId,
      intent = dockApp.data.intent,
      imagePath = dockApp.data.imagePath,
      position = dockApp.data.position)

  def toRepositoryDockApp(dockApp: DockApp): RepoDockApp =
    RepoDockApp(
      id = dockApp.id,
      data = RepoDockAppData(
        name = dockApp.name,
        cardType = dockApp.cardType,
        collectionId = dockApp.collectionId,
        intent = dockApp.intent,
        imagePath = dockApp.imagePath,
        position = dockApp.position))

  def toRepositoryDockApp(request: UpdateDockAppRequest): RepoDockApp =
    RepoDockApp(
      id = request.id,
      data = RepoDockAppData(
        name = request.name,
        cardType = request.cardType,
        collectionId = request.collectionId,
        intent = request.intent,
        imagePath = request.imagePath,
        position = request.position))

  def toRepositoryDockAppData(request: AddDockAppRequest): RepoDockAppData =
    RepoDockAppData(
      name = request.name,
      cardType = request.cardType,
      collectionId = request.collectionId,
      intent = request.intent,
      imagePath = request.imagePath,
      position = request.position)
}
