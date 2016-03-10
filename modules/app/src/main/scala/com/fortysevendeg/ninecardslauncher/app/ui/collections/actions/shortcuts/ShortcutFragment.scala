package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut
import com.fortysevendeg.ninecardslauncher2.R

import scalaz.concurrent.Task

class ShortcutFragment
  extends BaseActionFragment
  with ShortcutComposer
  with NineCardIntentConversions {

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    initUi.run
    loadShortCuts()
  }

  private[this] def loadShortCuts(): Unit =
    Task.fork(di.deviceProcess.getAvailableShortcuts.run).resolveAsyncUi(
      onPreTask = () => showLoading,
      onResult = (shortcut: Seq[Shortcut]) => addShortcuts(shortcut, shortcut => {
        unreveal().run
        getActivity.startActivityForResult(shortcut.intent, shortcutAdded)
      }),
      onException = (ex: Throwable) => showError(R.string.errorLoadingShortcuts, loadShortCuts())
    )

}


