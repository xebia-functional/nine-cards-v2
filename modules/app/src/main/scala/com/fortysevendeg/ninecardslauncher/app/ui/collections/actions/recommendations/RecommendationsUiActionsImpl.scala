package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsPagerPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher.process.theme.models.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

trait RecommendationsUiActionsImpl
  extends RecommendationsUiActions
  with Styles {

  self: TypedFindView with BaseActionFragment =>

  val collectionsPresenter: CollectionsPagerPresenter

  implicit val presenter: RecommendationsPresenter

  lazy val recycler = findView(TR.actions_recycler)

  def loadBackgroundColor = theme.get(CardLayoutBackgroundColor)

  override def initialize(): Ui[Any] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.recommendations) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  override def showLoading(): Ui[Any] = (loading <~ vVisible) ~ (recycler <~ vGone)

  override def showErrorLoadingRecommendationInScreen(): Ui[Any] =
    showMessageInScreen(R.string.errorLoadingRecommendations, error = true, presenter.loadRecommendations())

  override def loadRecommendations(recommendations: Seq[RecommendedApp]): Ui[Any] = {
    val adapter = RecommendationsAdapter(recommendations)
    (recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)
  }

  override def recommendationAdded(card: AddCardRequest): Ui[Any] = {
    collectionsPresenter.addCards(Seq(card))
    unreveal()
  }

  override def showContactUsError(): Ui[Any] =
    uiShortToast(R.string.contactUsError)

}
