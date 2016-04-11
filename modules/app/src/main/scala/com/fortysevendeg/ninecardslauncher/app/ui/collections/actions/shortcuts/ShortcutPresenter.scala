package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class ShortcutPresenter(actions: ShortcutIuActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Presenter {

  def initialize(): Unit = {
    actions.initialize().run
    loadShortcuts()
  }

  def loadShortcuts(): Unit =
    Task.fork(di.deviceProcess.getAvailableShortcuts.run).resolveAsyncUi(
      onPreTask = () => actions.showLoading(),
      onResult =  actions.loadShortcuts,
      onException = (ex: Throwable) => actions.showLoadingShortcutsError()
    )

  def configureShortcut(shortcut: Shortcut) = {
    actions.close().run
    activityContextWrapper.getOriginal.startActivityForResult(shortcut.intent, shortcutAdded)
  }

}

trait ShortcutIuActions {

  def initialize(): Ui[Any]

  def showLoading(): Ui[Any]

  def close(): Ui[Any]

  def loadShortcuts(shortcuts: Seq[Shortcut]): Ui[Any]

  def showLoadingShortcutsError(): Ui[Any]

}
