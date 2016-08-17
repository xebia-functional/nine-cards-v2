package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.editmoment

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import macroid._

class EditMomentPresenter (actions: EditMomentActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter {

  def initialize(): Unit = actions.initialize().run

}

trait EditMomentActions {

  def initialize(): Ui[Any]

}