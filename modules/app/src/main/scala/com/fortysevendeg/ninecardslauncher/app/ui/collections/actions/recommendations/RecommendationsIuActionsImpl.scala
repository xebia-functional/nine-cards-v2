package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsPagerPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

trait RecommendationsIuActionsImpl
  extends RecommendationsIuActions
  with Styles {

  self: TypedFindView with BaseActionFragment =>

  val collectionsPresenter: CollectionsPagerPresenter

  implicit val presenter: RecommendationsPresenter

  lazy val recycler = Option(findView(TR.actions_recycler))

  override def initialize(): Ui[Any] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.recommendations) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  override def showLoading(): Ui[Any] = (loading <~ vVisible) ~ (recycler <~ vGone)

  override def showLoadingRecommendationError(): Ui[Any] =
    showError(R.string.errorLoadingRecommendations, presenter.loadRecommendations())

  override def loadRecommendations(recommendations: Seq[RecommendedApp]): Ui[Any] = {
    val adapter = new RecommendationsAdapter(recommendations)
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

}
