package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FragmentUiContext, NineCardIntentConversions, UiContext}
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._

import scalaz.concurrent.Task

class RecommendationsFragment
  extends BaseActionFragment
  with RecommendationsComposer
  with NineCardIntentConversions {

  implicit lazy val di: Injector = new Injector

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  lazy val maybeCategory = Option(getString(Seq(getArguments), RecommendationsFragment.categoryKey, null))

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(initUi)
    val task = maybeCategory map { category =>
      di.recommendationsProcess.getRecommendedAppsByCategory(category, packages)
    } getOrElse di.recommendationsProcess.getRecommendedAppsByPackages(packages, packages)

    Task.fork(task.run).resolveAsyncUi(
      onPreTask = () => showLoading,
      onResult = (recommendations: Seq[RecommendedApp]) => addRecommendations(recommendations, onInstallNowClick),
      onException = (_) => showGeneralError
    )
  }
}

object RecommendationsFragment {
  val categoryKey = "category"
}


