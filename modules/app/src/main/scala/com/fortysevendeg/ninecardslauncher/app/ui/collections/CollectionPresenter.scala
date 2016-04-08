package com.fortysevendeg.ninecardslauncher.app.ui.collections

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class CollectionPresenter(actions: CollectionUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Presenter {

  def reorderCard(collectionId: Int, cardId: Int, position: Int) = {
    Task.fork(di.collectionProcess.reorderCard(collectionId, cardId, position).run).resolveAsyncUi(
      onResult = (_) => actions.reloadCards()
    )
  }

}

trait CollectionUiActions {

  def reloadCards(): Ui[Any]

}