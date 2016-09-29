package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.widgets

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import cards.nine.process.device.models.AppsWithWidgets
import macroid._


class WidgetsPresenter(actions: WidgetsUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs {

  def initialize(): Unit = {
    actions.initialize().run
    loadWidgets()
  }

  def loadWidgets(): Unit = {
    di.deviceProcess.getWidgets.resolveAsyncUi2(
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