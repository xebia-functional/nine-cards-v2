package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.widgets

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import macroid._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.process.device.models.Widget

import scalaz.concurrent.Task

class WidgetsPresenter(actions: WidgetsUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter {

  def initialize(): Unit = {
    Task.fork(di.deviceProcess.getWidgets.run).resolveAsyncUi(
      onPreTask = () => actions.initialize() ~ actions.showLoading(),
      onResult = (widgets: Seq[Widget]) => actions.loadWidgets(widgets)
    )
  }

  def close(): Unit = actions.close().run

}

trait WidgetsUiActions {

  def initialize(): Ui[Any]

  def loadWidgets(widgets: Seq[Widget]): Ui[Any]

  def showLoading(): Ui[Any]

  def close(): Ui[Any]

}