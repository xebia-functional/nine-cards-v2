package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher.process.theme.models.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher2.R
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
