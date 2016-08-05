package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.collections.CollectionCardsStyles
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CollectionDetailTextCardColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._
import macroid.FullDsl._

case class RecommendationsAdapter(recommendations: Seq[RecommendedApp])
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], presenter: RecommendationsPresenter, theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderRecommendationsLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRecommendationsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.recommendations_item, parent, false).asInstanceOf[ViewGroup]
    ViewHolderRecommendationsLayoutAdapter(view)
  }

  override def getItemCount: Int = recommendations.size

  override def onBindViewHolder(viewHolder: ViewHolderRecommendationsLayoutAdapter, position: Int): Unit = {
    val recommendation = recommendations(position)
    viewHolder.bind(recommendation, position).run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}

case class ViewHolderRecommendationsLayoutAdapter(content: ViewGroup)
  (implicit context: ActivityContextWrapper, presenter: RecommendationsPresenter, val theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView
  with CollectionCardsStyles {

  lazy val root = findView(TR.recommendation_item_layout)

  lazy val icon = findView(TR.recommendation_item_icon)

  lazy val name = findView(TR.recommendation_item_name)

  lazy val downloads = findView(TR.recommendation_item_downloads)

  lazy val tag = findView(TR.recommendation_item_tag)

  lazy val stars = findView(TR.recommendation_item_stars)

  lazy val screenshots = Seq(
    findView(TR.recommendation_item_screenshot1),
    findView(TR.recommendation_item_screenshot2),
    findView(TR.recommendation_item_screenshot3))

  lazy val description = findView(TR.recommendation_item_description)

  lazy val installNow = findView(TR.recommendation_item_install_now)

  ((root <~ cardRootStyle) ~
    (name <~ textStyle) ~
    (downloads <~ textStyle) ~
    (description <~ textStyle) ~
    (tag <~ textStyle) ~
    (installNow <~ buttonStyle)).run

  def bind(recommendedApp: RecommendedApp, position: Int)(implicit uiContext: UiContext[_]): Ui[_] = {
    val screensUi: Seq[Ui[_]] = (screenshots zip recommendedApp.screenshots) map {
      case (view, screenshot) => view <~ ivUri(screenshot)
    }
    (icon <~ ivUri(recommendedApp.icon getOrElse "")) ~ // If icon don't exist ivUri will solve the problem
      (stars <~ ivSrc(getStarDrawable(recommendedApp.stars))) ~
      (name <~ tvText(recommendedApp.title)) ~
      (downloads <~ tvText(recommendedApp.downloads)) ~
      (description <~ (recommendedApp.description map (d => tvText(d) + vVisible) getOrElse vGone)) ~
      (tag <~ tvText(if (recommendedApp.free) resGetString(R.string.free) else "")) ~
      (content <~ vTag(position)) ~
      Ui.sequence(screensUi: _*) ~
      (installNow <~ On.click(Ui(presenter.installNow(recommendedApp))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

  private[this] def getStarDrawable(value: Double) = value match {
    case v if v < 1.1 => R.drawable.recommendations_starts_01
    case v if v < 1.6 => R.drawable.recommendations_starts_01_5
    case v if v < 2.1 => R.drawable.recommendations_starts_02
    case v if v < 2.6 => R.drawable.recommendations_starts_02_5
    case v if v < 3.1 => R.drawable.recommendations_starts_03
    case v if v < 3.6 => R.drawable.recommendations_starts_03_5
    case v if v < 4.1 => R.drawable.recommendations_starts_04
    case v if v < 4.6 => R.drawable.recommendations_starts_04_5
    case _ => R.drawable.recommendations_starts_05
  }

}