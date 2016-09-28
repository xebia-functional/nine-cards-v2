package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs.GroupCollectionsUiListener
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiExtensions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AllAppsCategory, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher2.R

class AppsFragment
  extends BaseActionFragment
  with AppsIuActions
  with AppsDOM
  with AppsUiListener
  with Conversions
  with UiExtensions { self =>

  val allApps = AllAppsCategory

  lazy val appsJobs = AppsJobs(
    category = NineCardCategory(getString(Seq(getArguments), AppsFragment.categoryKey, AllAppsCategory.name)),
    actions = self)

  override def getLayoutId: Int = R.layout.list_action_with_scroller_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    appsJobs.initialize().resolveAsync()
  }

  override def onDestroy(): Unit = {
    appsJobs.destroy().resolveAsync()
    super.onDestroy()
  }

  override def loadApps(filter: AppsFilter): Unit =
    appsJobs.loadApps(filter).resolveAsyncServiceOr(_ => appsJobs.showErrorLoadingApps(filter))

  override def addApp(app: App): Unit = {
    getActivity match {
      case listener: GroupCollectionsUiListener =>
        listener.addCards(Seq(toAddCardRequest(app)))
        appsJobs.close().resolveAsync()
      case _ =>
    }
  }

  override def swapFilter(): Unit = appsJobs.swapFilter().resolveAsync()
}

object AppsFragment {
  val categoryKey = "category"
}

sealed trait AppsFilter

case object AllApps extends AppsFilter

case object AppsByCategory extends AppsFilter
