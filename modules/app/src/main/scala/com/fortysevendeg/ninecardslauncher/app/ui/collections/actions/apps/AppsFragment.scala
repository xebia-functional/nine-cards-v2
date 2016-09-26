package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs.GroupCollectionsJobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiExtensions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AllAppsCategory, NineCardCategory}
import com.fortysevendeg.ninecardslauncher2.R

class AppsFragment(implicit gcj: GroupCollectionsJobs)
  extends BaseActionFragment
  with AppsIuActionsImpl
  with UiExtensions { self =>

  val allApps = AllAppsCategory

  override lazy val presenter = AppsPresenter(
    category = NineCardCategory(getString(Seq(getArguments), AppsFragment.categoryKey, AllAppsCategory.name)),
    actions = self)

  override val groupCollectionsJobs: GroupCollectionsJobs = gcj

  override def getLayoutId: Int = R.layout.list_action_with_scroller_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    presenter.initialize()
  }

  override def onDestroy(): Unit = {
    presenter.destroy()
    super.onDestroy()
  }
}

object AppsFragment {
  val categoryKey = "category"
}

sealed trait AppsFilter

case object AllApps extends AppsFilter

case object AppsByCategory extends AppsFilter
