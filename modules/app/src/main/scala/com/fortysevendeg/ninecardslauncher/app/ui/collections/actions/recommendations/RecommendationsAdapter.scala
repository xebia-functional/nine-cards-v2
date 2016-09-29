package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.styles.CollectionCardsStyles
import cards.nine.process.recommendations.models.RecommendedApp
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class RecommendationsAdapter(recommendations: Seq[RecommendedApp], onInstall: (RecommendedApp) => Unit)
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderRecommendationsLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRecommendationsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.recommendations_item, parent, false).asInstanceOf[ViewGroup]
    ViewHolderRecommendationsLayoutAdapter(view)
  }

  override def getItemCount: Int = recommendations.size

  override def onBindViewHolder(viewHolder: ViewHolderRecommendationsLayoutAdapter, position: Int): Unit = {
    val recommendation = recommendations(position)
    viewHolder.bind(recommendation, onInstall).run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}

case class ViewHolderRecommendationsLayoutAdapter(content: ViewGroup)
  (implicit context: ActivityContextWrapper, val theme: NineCardsTheme)
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

  lazy val installNow = findView(TR.recommendation_item_install_now)

  ((root <~ cardRootStyle) ~
    (name <~ textStyle) ~
    (downloads <~ leftDrawableTextStyle(R.drawable.icon_download)) ~
    (tag <~ textStyle) ~
    (installNow <~ buttonStyle)).run

  def bind(recommendedApp: RecommendedApp, onInstall: (RecommendedApp) => Unit)(implicit uiContext: UiContext[_]): Ui[_] = {
    val screensUi: Seq[Ui[_]] = (screenshots zip recommendedApp.screenshots) map {
      case (view, screenshot) => view <~ ivUri(screenshot)
    }
    (icon <~ ivUri(recommendedApp.icon getOrElse "")) ~ // If icon don't exist ivUri will solve the problem
      (stars <~ ivSrc(tintDrawable(getStarDrawable(recommendedApp.stars)))) ~
      (name <~ tvText(recommendedApp.title)) ~
      (downloads <~ tvText(recommendedApp.downloads)) ~
      (tag <~ tvText(if (recommendedApp.free) resGetString(R.string.free) else "")) ~
      Ui.sequence(screensUi: _*) ~
      (installNow <~ On.click(Ui(onInstall(recommendedApp))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

  private[this] def getStarDrawable(value: Double): Int = value match {
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