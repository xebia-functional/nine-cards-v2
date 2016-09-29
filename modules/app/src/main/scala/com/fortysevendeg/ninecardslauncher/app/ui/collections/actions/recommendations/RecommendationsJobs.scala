package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.commons.types.NineCardCategory
import cards.nine.process.recommendations.models.RecommendedApp
import macroid.ActivityContextWrapper

class RecommendationsJobs(
  category: NineCardCategory,
  packages: Seq[String],
  actions: RecommendationsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with NineCardIntentConversions {

  def initialize(): TaskService[Unit] = for {
    _ <- actions.initialize()
    _ <- loadRecommendations()
  } yield ()

  def installNow(app: RecommendedApp): TaskService[Unit] =
    for {
      _ <- di.launcherExecutorProcess.launchGooglePlay(app.packageName)
      _ <- actions.recommendationAdded(app)
    } yield ()

  def loadRecommendations(): TaskService[Unit] = {
    for {
      _ <- actions.showLoading()
      recommendations <- if (category.isAppCategory) {
        di.recommendationsProcess.getRecommendedAppsByCategory(category, packages)
      } else {
        di.recommendationsProcess.getRecommendedAppsByPackages(packages, packages)
      }
      _ <- actions.loadRecommendations(recommendations)
    } yield ()
  }

  def showErrorLoadingRecommendation(): TaskService[Unit] = actions.showErrorLoadingRecommendationInScreen()

  def showError(): TaskService[Unit] = actions.showContactUsError()

}
