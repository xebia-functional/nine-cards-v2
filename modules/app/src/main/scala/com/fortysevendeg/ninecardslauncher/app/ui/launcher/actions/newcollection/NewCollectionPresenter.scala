package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.newcollection

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCollectionRequest
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.{FreeCollectionType, NineCardCategory}
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class NewCollectionPresenter (actions: NewCollectionActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter {

  def initialize(): Unit = actions.initialize().run

  def saveCollection(maybeName: Option[String], maybeCategory: Option[NineCardCategory], maybeIndex: Option[Int]): Unit =
    (for {
      name <- maybeName
      category <- maybeCategory
      index <- maybeIndex
    } yield {
      val request = AddCollectionRequest(
        name = name,
        collectionType = FreeCollectionType,
        icon = category.getIconResource,
        themedColorIndex = index,
        appsCategory = None
      )
      Task.fork(di.collectionProcess.addCollection(request).run).resolveAsyncUi(
        onResult = (c) => actions.addCollection(c) ~ actions.close(),
        onException = (ex) => actions.showMessageContactUsError
      )
    }) getOrElse actions.showMessageFormFieldError.run

  def updateCategory(maybeCategory: Option[NineCardCategory]): Unit = {
    maybeCategory map { category =>
      actions.updateCategory(category).run
    } getOrElse actions.showMessageContactUsError.run
  }

  def updateColor(maybeIndexColor: Option[Int]): Unit = {
    maybeIndexColor map { indexColor =>
      actions.updateColor(indexColor).run
    } getOrElse actions.showMessageContactUsError.run
  }

}

trait NewCollectionActions {

  def initialize(): Ui[Any]

  def updateCategory(nineCardCategory: NineCardCategory): Ui[Any]

  def updateColor(indexColor: Int): Ui[Any]

  def addCollection(collection: Collection): Ui[Any]

  def showMessageContactUsError: Ui[Any]

  def showMessageFormFieldError: Ui[Any]

  def close(): Ui[Any]

}
