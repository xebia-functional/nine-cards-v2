package com.fortysevendeg.ninecardslauncher.app.ui.applinks

import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, Ui}

class AppLinksReceiverUiActions(dom: AppLinksReceiverDOM)(implicit contextWrapper: ActivityContextWrapper) {

  def showLinkNotSupportedMessage(): TaskService[Unit] =
    uiShortToast2(R.string.linkNotSupportedError).toService

  def exit(): TaskService[Unit] =
    Ui(contextWrapper.original.get foreach (_.finish())).toService

}



trait AppLinksReceiverDOM {



}