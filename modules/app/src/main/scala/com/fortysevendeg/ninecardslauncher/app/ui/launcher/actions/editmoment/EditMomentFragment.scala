package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.editmoment

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher2.R

class EditMomentFragment(implicit lPresenter: LauncherPresenter)
  extends BaseActionFragment
  with EditMomentActionsImpl
  with NineCardIntentConversions { self =>

  lazy val moment = getString(Seq(getArguments), EditMomentFragment.momentKey, javaNull)

  lazy val launcherPresenter = lPresenter

  lazy val editPresenter = new EditMomentPresenter(self)

  override def getLayoutId: Int = R.layout.edit_moment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    editPresenter.initialize()
  }

}

object EditMomentFragment {

  val momentKey = "moment"

}

