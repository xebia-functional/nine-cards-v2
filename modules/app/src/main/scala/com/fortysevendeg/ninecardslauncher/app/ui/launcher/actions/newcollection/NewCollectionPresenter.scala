package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.newcollection

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCollectionRequest
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.{NineCardCategory, FreeCollectionType}
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class NewCollectionPresenter (actions: NewCollectionActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter {

  def saveCollection(maybeName: Option[String], maybeCategory: Option[NineCardCategory], maybeIndex: Option[Int]): Ui[Any] =
    (for {
      name <- maybeName
      category <- maybeCategory
      index <- maybeIndex
    } yield {
      Ui {
        val request = AddCollectionRequest(
          name = name,
          collectionType = FreeCollectionType,
          icon = category.getIconResource,
          themedColorIndex = index,
          appsCategory = None
        )
        Task.fork(di.collectionProcess.addCollection(request).run).resolveAsyncUi(
          onResult = (c) => actions.addCollection(c),
          onException = (ex) => actions.showMessageContactUsError
        )
      }
    }) getOrElse actions.showMessageFormFieldError

}

trait NewCollectionActions {

  def addCollection(collection: Collection): Ui[Any]

  def showMessageContactUsError: Ui[Any]

  def showMessageFormFieldError: Ui[Any]

}
