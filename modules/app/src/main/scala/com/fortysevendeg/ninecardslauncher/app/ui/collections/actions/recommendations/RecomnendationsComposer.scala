package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{NineCardIntentConversions, LauncherExecutor, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.CardType
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

trait RecommendationsComposer
  extends Styles
  with LauncherExecutor
  with NineCardIntentConversions {

  self: TypedFindView with BaseActionFragment =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  def initUi: Ui[_] =
    (toolbar <~
      tbTitle(R.string.recommendations) <~
      toolbarStyle(colorPrimary) <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def addRecommendations(recommendations: Seq[RecommendedApp], clickListener: (RecommendedApp) => Ui[_])(implicit uiContext: UiContext[_]) = {
    val adapter = new RecommendationsAdapter(recommendations, clickListener)
    (recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)
  }

  def onInstallNowClick(app: RecommendedApp): Ui[_] =
    Ui {
      launchGooglePlay(app.packageName)
      val card = AddCardRequest(
        term = app.title,
        packageName = Option(app.packageName),
        cardType = CardType.noInstalledApp,
        intent = toNineCardIntent(app),
        imagePath = ""
      )
      activity[CollectionsDetailsActivity] foreach (_.addCards(Seq(card)))
    } ~
      unreveal()

}

case class ViewHolderRecommendationsLayoutAdapter(content: ViewGroup, clickListener: (RecommendedApp) => Ui[_])(implicit context: ActivityContextWrapper)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.recommendation_item_icon))

  lazy val name = Option(findView(TR.recommendation_item_name))

  lazy val downloads = Option(findView(TR.recommendation_item_downloads))

  lazy val tag = Option(findView(TR.recommendation_item_tag))

  lazy val stars = Option(findView(TR.recommendation_item_stars))

  lazy val screenshots = Seq(
    Option(findView(TR.recommendation_item_screenshot1)),
    Option(findView(TR.recommendation_item_screenshot2)),
    Option(findView(TR.recommendation_item_screenshot3)))

  lazy val description = Option(findView(TR.recommendation_item_description))

  lazy val installNow = Option(findView(TR.recommendation_item_install_now))

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
      (content <~ vIntTag(position)) ~
      Ui.sequence(screensUi: _*) ~
      (installNow <~ On.click(clickListener(recommendedApp)))
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