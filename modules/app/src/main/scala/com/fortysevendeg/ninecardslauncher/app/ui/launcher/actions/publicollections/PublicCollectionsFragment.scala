package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.TypeSharedCollection
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Ui

class PublicCollectionsFragment(implicit lPresenter: LauncherPresenter)
  extends BaseActionFragment
  with PublicCollectionsActionsImpl
  with NineCardIntentConversions { self =>

  lazy val presenter: PublicCollectionsPresenter = new PublicCollectionsPresenter(self)

  lazy val launcherPresenter = lPresenter

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    presenter.initialize()
  }
}




