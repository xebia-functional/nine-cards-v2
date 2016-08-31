package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.widgets

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TasksOps._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppsWithWidgets
import macroid._

import scalaz.concurrent.Task

class WidgetsPresenter(actions: WidgetsUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter {

  def initialize(): Unit = {
    actions.initialize().run
    loadWidgets()
  }

  def loadWidgets(): Unit = {
    Task.fork(di.deviceProcess.getWidgets.value).resolveAsyncUi(
      onPreTask = () => actions.showLoading(),
      onResult = (widgets: Seq[AppsWithWidgets]) => actions.loadWidgets(widgets),
      onException = (_) => actions.showErrorLoadingWidgetsInScreen()
    )
  }

  def close(): Unit = actions.close().run

}

trait WidgetsUiActions {

  def initialize(): Ui[Any]

  def loadWidgets(appsWithWidgets: Seq[AppsWithWidgets]): Ui[Any]

  def showLoading(): Ui[Any]

  def showErrorLoadingWidgetsInScreen(): Ui[Any]

  def close(): Ui[Any]

}