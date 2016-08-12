package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher2.R

class ShortcutFragment
  extends BaseActionFragment
  with ShortcutUiActionsImpl
  with NineCardIntentConversions { self =>

  override lazy val presenter = new ShortcutPresenter(self)

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    presenter.initialize()
  }

}
