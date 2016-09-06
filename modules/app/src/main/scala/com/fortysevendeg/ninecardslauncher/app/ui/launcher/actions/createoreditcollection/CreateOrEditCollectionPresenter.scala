package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.createoreditcollection

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TasksOps._
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCollectionRequest, EditCollectionRequest}
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.FreeCollectionType
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class CreateOrEditCollectionPresenter(actions: CreateOrEditCollectionActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs {

  def initialize(maybeCollectionId: Option[String]): Unit = {
    actions.initialize().run
    maybeCollectionId match {
      case Some(collectionId) => findCollection(collectionId.toInt)
      case None => actions.initializeNewCollection().run
    }
  }

  def findCollection(collectionId: Int): Unit =
    Task.fork(di.collectionProcess.getCollectionById(collectionId).value).resolveAsyncUi(
      onResult = {
        case Some(collection) => actions.initializeEditCollection(collection)
        case _ => actions.showMessageContactUsError
      },
      onException = (ex) => actions.showMessageContactUsError
    )

  def editCollection(collection: Collection, maybeName: Option[String], maybeIcon: Option[String], maybeIndex: Option[Int]): Unit =
    (for {
      name <- maybeName
      icon <- maybeIcon
      index <- maybeIndex
    } yield {
      val request = EditCollectionRequest(
        name = name,
        icon = icon,
        themedColorIndex = index,
        appsCategory = collection.appsCategory
      )
      Task.fork(di.collectionProcess.editCollection(collection.id, request).value).resolveAsyncUi(
        onResult = (c) => actions.editCollection(c) ~ actions.close(),
        onException = (ex) => actions.showMessageContactUsError
      )
    }) getOrElse actions.showMessageFormFieldError.run

  def saveCollection(maybeName: Option[String], maybeIcon: Option[String], maybeIndex: Option[Int]): Unit =
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
        moment = None
      )
      Task.fork(di.collectionProcess.addCollection(request).value).resolveAsyncUi(
        onResult = (c) => actions.addCollection(c) ~ actions.close(),
        onException = (ex) => actions.showMessageContactUsError
      )
    }) getOrElse actions.showMessageFormFieldError.run

  def updateIcon(maybeIcon: Option[String]): Unit = {
    maybeIcon map { icon =>
      actions.updateIcon(icon).run
    } getOrElse actions.showMessageContactUsError.run
  }

  def updateColor(maybeIndexColor: Option[Int]): Unit = {
    maybeIndexColor map { indexColor =>
      actions.updateColor(indexColor).run
    } getOrElse actions.showMessageContactUsError.run
  }

  def changeColor(maybeColor: Option[Int]): Unit = maybeColor map { color =>
    actions.showColorDialog(color).run
  } getOrElse actions.showMessageContactUsError.run

  def changeIcon(maybeIcon: Option[String]): Unit = maybeIcon map { icon =>
    actions.showIconDialog(icon).run
  } getOrElse actions.showMessageContactUsError.run

}

trait CreateOrEditCollectionActions {

  def initialize(): Ui[Any]

  def editCollection(collection: Collection): Ui[Any]

  def updateIcon(iconName: String): Ui[Any]

  def updateColor(indexColor: Int): Ui[Any]

  def addCollection(collection: Collection): Ui[Any]

  def initializeNewCollection(): Ui[Any]

  def initializeEditCollection(collection: Collection): Ui[Any]

  def showMessageContactUsError: Ui[Any]

  def showMessageFormFieldError: Ui[Any]

  def showColorDialog(color: Int): Ui[Any]

  def showIconDialog(icon: String): Ui[Any]

  def close(): Ui[Any]

}
