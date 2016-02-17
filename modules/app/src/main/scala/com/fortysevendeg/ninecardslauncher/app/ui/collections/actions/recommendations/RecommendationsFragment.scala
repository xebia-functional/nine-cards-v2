package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FragmentUiContext, UiContext}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._

import scalaz.concurrent.Task

class RecommendationsFragment
  extends BaseActionFragment
  with RecommendationsComposer
  with NineCardIntentConversions {

  lazy val nineCardCategory = NineCardCategory(getString(Seq(getArguments), RecommendationsFragment.categoryKey, ""))

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(initUi)
    loadRecommendations()
  }

  private[this] def loadRecommendations(): Unit = {
    val task = if (nineCardCategory.isAppCategory) {
      di.recommendationsProcess.getRecommendedAppsByCategory(nineCardCategory, packages)
    } else {
      di.recommendationsProcess.getRecommendedAppsByPackages(packages, packages)
    }
    Task.fork(task.run).resolveAsyncUi(
      onPreTask = () => showLoading,
      onResult = (recommendations: Seq[RecommendedApp]) => addRecommendations(recommendations, onInstallNowClick),
      onException = (_) => showError(R.string.errorLoadingRecommendations, loadRecommendations()))
  }
}

object RecommendationsFragment {
  val categoryKey = "category"
}


