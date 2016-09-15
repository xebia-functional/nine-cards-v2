package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.types.{NineCardCategory, NoInstalledAppCardType}
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import macroid.{ActivityContextWrapper, Ui}


class RecommendationsPresenter(
  category: NineCardCategory,
  packages: Seq[String],
  actions: RecommendationsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with NineCardIntentConversions {

  def initialize(): Unit = {
    actions.initialize().run
    loadRecommendations()
  }

  def installNow(app: RecommendedApp): Unit = {
    di.launcherExecutorProcess.launchGooglePlay(app.packageName).resolveAsyncUi2(
      onException = _ => actions.showContactUsError())
    val card = AddCardRequest(
      term = app.title,
      packageName = Option(app.packageName),
      cardType = NoInstalledAppCardType,
      intent = toNineCardIntent(app),
      imagePath = "")
    actions.recommendationAdded(card).run
  }

  def loadRecommendations(): Unit = {
    val task = if (category.isAppCategory) {
      di.recommendationsProcess.getRecommendedAppsByCategory(category, packages)
    } else {
      di.recommendationsProcess.getRecommendedAppsByPackages(packages, packages)
    }
    task.resolveAsyncUi2(
      onPreTask = () => actions.showLoading(),
      onResult = (recommendations: Seq[RecommendedApp]) => actions.loadRecommendations(recommendations),
      onException = (_) => actions.showErrorLoadingRecommendationInScreen())
  }

}

trait RecommendationsUiActions {

  def initialize(): Ui[Any]

  def showLoading(): Ui[Any]

  def showErrorLoadingRecommendationInScreen(): Ui[Any]

  def loadRecommendations(recommendations: Seq[RecommendedApp]): Ui[Any]

  def recommendationAdded(card: AddCardRequest): Ui[Any]

  def showContactUsError(): Ui[Any]

}