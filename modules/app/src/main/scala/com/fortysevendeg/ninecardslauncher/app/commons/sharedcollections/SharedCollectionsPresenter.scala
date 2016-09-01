package com.fortysevendeg.ninecardslauncher.app.commons.sharedcollections

import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, Presenter}
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection

trait SharedCollectionsPresenter
  extends Presenter
  with LauncherExecutor {

  def saveSharedCollection(sharedCollection: SharedCollection): Unit

}
