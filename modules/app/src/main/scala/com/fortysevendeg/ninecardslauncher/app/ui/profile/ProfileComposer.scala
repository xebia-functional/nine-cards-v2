package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.Tab
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.{DefaultItemAnimator, GridLayoutManager}
import android.view.View
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionItemDecorator
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.PathMorphDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons
import com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters.{AccountsAdapter, SubscriptionsAdapter, PublicationsAdapter}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._
import macroid.FullDsl._

trait ProfileComposer
  extends ProfileStyles
  with TabLayout.OnTabSelectedListener {

  self: AppCompatActivity
    with TypedFindView
    with SystemBarsTint
    with Contexts[AppCompatActivity]
    with ProfileTabListener =>

  lazy val barLayout = Option(findView(TR.profile_appbar))

  lazy val toolbar = Option(findView(TR.profile_toolbar))

  lazy val userContainer = Option(findView(TR.profile_user_container))

  lazy val userAvatar = Option(findView(TR.profile_user_avatar))

  lazy val userName = Option(findView(TR.profile_user_name))

  lazy val tabs = Option(findView(TR.profile_tabs))

  lazy val recyclerView = Option(findView(TR.profile_recycler))

  lazy val iconIndicatorDrawable = new PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  def initUi(implicit uiContext: UiContext[_], theme: NineCardsTheme): Ui[_] =
    (userAvatar <~ menuAvatarStyle) ~
      (tabs <~ tlAddTabs(
        (getString(R.string.publications), PublicationsTab),
        (getString(R.string.subscriptions), SubscriptionsTab),
        (getString(R.string.accounts), AccountsTab))) ~
      (tabs <~ tlSetListener(this)) ~
      (recyclerView <~
        rvLayoutManager(new GridLayoutManager(activityContextWrapper.application, 1)) <~
        rvFixedSize <~
        rvAddItemDecoration(new CollectionItemDecorator) <~
        rvItemAnimator(new DefaultItemAnimator)) ~
      Ui(onProfileTabSelected(PublicationsTab))

  def userProfile(name: String, avatarUrl: String)(implicit uiContext: UiContext[_]): Ui[_] =
    (userName <~ tvText(name)) ~
      (userAvatar <~ ivUri(avatarUrl))

  def handleToolbarVisibility(percentage: Float): Ui[_] = toolbar match {
    case Some(t) if percentage >= 0.5 && t.getVisibility == View.VISIBLE => toolbar <~ SnailsCommons.fadeOut()
    case Some(t) if percentage < 0.5 && t.getVisibility == View.INVISIBLE => toolbar <~ SnailsCommons.fadeIn()
    case _ => Ui.nop
  }

  def handleProfileVisibility(percentage: Float)(implicit context: ContextWrapper): Ui[_] = {
    val alpha = if (percentage <= 0.5f) 1f - (percentage * 2)  else 0f
    userContainer <~ vAlpha(alpha)
  }

  def setPublicationsAdapter(items: Seq[String])
      (implicit uiContext: UiContext[_], theme: NineCardsTheme) =
    recyclerView <~ rvAdapter(new PublicationsAdapter(items))

  def setSubscriptionsAdapter(items: Seq[String])
      (implicit uiContext: UiContext[_], theme: NineCardsTheme) =
    recyclerView <~ rvAdapter(new SubscriptionsAdapter(items))

  def setAccountsAdapter(items: Seq[String])
      (implicit uiContext: UiContext[_], theme: NineCardsTheme) =
    recyclerView <~ rvAdapter(new AccountsAdapter(items))

  override def onTabReselected(tab: Tab): Unit = {}

  override def onTabUnselected(tab: Tab): Unit = {}

  override def onTabSelected(tab: Tab): Unit = {
    tab.getTag match {
      case tab: ProfileTab => onProfileTabSelected(tab)
      case _ =>
    }
  }
}
