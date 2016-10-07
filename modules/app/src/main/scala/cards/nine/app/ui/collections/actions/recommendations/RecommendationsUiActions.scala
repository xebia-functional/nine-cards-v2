package cards.nine.app.ui.collections.actions.recommendations

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.process.recommendations.models.RecommendedApp
import cards.nine.process.theme.models.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher.R
import macroid._

trait RecommendationsUiActions
  extends Styles {

  self: BaseActionFragment with RecommendationsDOM with RecommendationsUiListener =>

  def loadBackgroundColor = theme.get(CardLayoutBackgroundColor)

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.recommendations) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)).toService

  def showLoading(): TaskService[Unit] = ((loading <~ vVisible) ~ (recycler <~ vGone)).toService

  def showErrorLoadingRecommendationInScreen(): TaskService[Unit]  =
    showMessageInScreen(R.string.errorLoadingRecommendations, error = true, loadRecommendations()).toService

  def loadRecommendations(recommendations: Seq[RecommendedApp]): TaskService[Unit] = {
    val adapter = RecommendationsAdapter(recommendations, installApp)
    ((recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)).toService
  }

  def recommendationAdded(app: RecommendedApp): TaskService[Unit] = TaskService.right(addApp(app))

  def close(): TaskService[Unit] = unreveal().toService

  def showContactUsError(): TaskService[Unit] =
    uiShortToast2(R.string.contactUsError).toService

}
