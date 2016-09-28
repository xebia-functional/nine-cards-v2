package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.{Conversions, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs.GroupCollectionsUiListener
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.recommendations.RecommendedAppsConfigurationException
import com.fortysevendeg.ninecardslauncher2.R

class RecommendationsFragment
  extends BaseActionFragment
  with RecommendationsUiActions
  with RecommendationsDOM
  with RecommendationsUiListener
  with Conversions
  with NineCardIntentConversions { self =>

  lazy val nineCardCategory = NineCardCategory(getString(Seq(getArguments), RecommendationsFragment.categoryKey, ""))

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  lazy val recommendationsJobs = new RecommendationsJobs(nineCardCategory, packages, self)

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int = loadBackgroundColor

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    recommendationsJobs.initialize().resolveAsyncServiceOr(onError)
  }

  override def loadRecommendations(): Unit = recommendationsJobs.loadRecommendations().resolveAsyncServiceOr(onError)

  override def addApp(app: RecommendedApp): Unit = getActivity match {
    case activity: GroupCollectionsUiListener => activity.addCards(Seq(toAddCardRequest(app)))
    case _ =>
  }

  override def installApp(app: RecommendedApp): Unit =
    recommendationsJobs.installNow(app).resolveAsyncServiceOr(_ => recommendationsJobs.showError())

  private[this] def onError(e: Throwable): TaskService[Unit] = e match {
    case e: RecommendedAppsConfigurationException =>
      AppLog.invalidConfigurationV2
      recommendationsJobs.showErrorLoadingRecommendation()
    case _ =>
      recommendationsJobs.showErrorLoadingRecommendation()
  }
}

object RecommendationsFragment {
  val categoryKey = "category"
}


