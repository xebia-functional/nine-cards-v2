package cards.nine.app.ui.launcher.actions.createoreditcollection

import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.commons.NineCardExtensions._
import cards.nine.models.types.FreeCollectionType
import cards.nine.process.collection.{AddCollectionRequest, EditCollectionRequest}
import cards.nine.process.commons.models.Collection
import macroid.ActivityContextWrapper

class CreateOrEditCollectionJobs(actions: CreateOrEditCollectionUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs {

  def initialize(maybeCollectionId: Option[String]): TaskService[Unit] = {

    def editCollection(collectionId: Int) =
      for {
        collection <- di.collectionProcess.getCollectionById(collectionId).resolveOption()
        _ <- actions.initializeEditCollection(collection)
      } yield ()

    for {
      theme <- getThemeTask
      _ <- actions.initialize(theme)
      _ <- maybeCollectionId match {
        case Some(collectionId) => editCollection(collectionId.toInt)
        case None => actions.initializeNewCollection()
      }
    } yield ()
  }

  def editCollection(collection: Collection, maybeName: Option[String], maybeIcon: Option[String], maybeIndex: Option[Int]): TaskService[Unit] =
    (for {
      name <- maybeName
      icon <- maybeIcon
      index <- maybeIndex
    } yield {
      val request = EditCollectionRequest(
        name = name,
        icon = icon,
        themedColorIndex = index,
        appsCategory = collection.appsCategory)
      for {
        collection <- di.collectionProcess.editCollection(collection.id, request)
        _ <- actions.editCollection(collection)
        _ <- actions.close()
      } yield ()
    }) getOrElse actions.showMessageFormFieldError

  def saveCollection(maybeName: Option[String], maybeIcon: Option[String], maybeIndex: Option[Int]): TaskService[Unit] =
    (for {
      name <- maybeName
      icon <- maybeIcon
      index <- maybeIndex
    } yield {
      val request = AddCollectionRequest(
        name = name,
        collectionType = FreeCollectionType,
        icon = icon,
        themedColorIndex = index,
        appsCategory = None,
        cards = Seq.empty,
        moment = None)
      for {
        collection <- di.collectionProcess.addCollection(request)
        _ <- actions.addCollection(collection)
        _ <- actions.close()
      } yield ()
    }) getOrElse actions.showMessageFormFieldError

  def updateIcon(maybeIcon: Option[String]): TaskService[Unit] = {
    maybeIcon map { icon =>
      actions.updateIcon(icon)
    } getOrElse actions.showMessageContactUsError
  }

  def updateColor(maybeIndexColor: Option[Int]): TaskService[Unit] = {
    maybeIndexColor map { indexColor =>
      actions.updateColor(indexColor)
    } getOrElse actions.showMessageContactUsError
  }

  def changeColor(maybeColor: Option[Int]): TaskService[Unit] = maybeColor map { color =>
    actions.showColorDialog(color)
  } getOrElse actions.showMessageContactUsError

  def changeIcon(maybeIcon: Option[String]): TaskService[Unit] = maybeIcon map { icon =>
    actions.showIconDialog(icon)
  } getOrElse actions.showMessageContactUsError

}