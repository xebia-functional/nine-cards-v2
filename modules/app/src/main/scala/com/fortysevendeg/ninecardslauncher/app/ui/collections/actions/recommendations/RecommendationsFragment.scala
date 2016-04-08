package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.collections.{CollectionsDetailsActivity, CollectionsPagerPresenter}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.types.{NoInstalledAppCardType, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Ui

import scalaz.concurrent.Task

class RecommendationsFragment(implicit collectionsPagerPresenter: CollectionsPagerPresenter)
  extends BaseActionFragment
  with RecommendationsComposer
  with NineCardIntentConversions {

  lazy val nineCardCategory = NineCardCategory(getString(Seq(getArguments), RecommendationsFragment.categoryKey, ""))

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    initUi.run
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

  def onInstallNowClick(app: RecommendedApp): Ui[_] =
    Ui {
      launchGooglePlay(app.packageName)
      val card = AddCardRequest(
        term = app.title,
        packageName = Option(app.packageName),
        cardType = NoInstalledAppCardType,
        intent = toNineCardIntent(app),
        imagePath = "")
      collectionsPagerPresenter.addCards(Seq(card))
    } ~
      unreveal()

}

object RecommendationsFragment {
  val categoryKey = "category"
}


