package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.Tab
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.PathMorphDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons
import com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters.{AccountsAdapter, SubscriptionsAdapter, PublicationsAdapter}
import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.AccountSync
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

  lazy val rootLayout = Option(findView(TR.profile_root))

  lazy val barLayout = Option(findView(TR.profile_appbar))

  lazy val toolbar = Option(findView(TR.profile_toolbar))

  lazy val userContainer = Option(findView(TR.profile_user_container))

  lazy val userAvatar = Option(findView(TR.profile_user_avatar))

  lazy val userName = Option(findView(TR.profile_user_name))

  lazy val userEmail = Option(findView(TR.profile_user_email))

  lazy val tabs = Option(findView(TR.profile_tabs))

  lazy val recyclerView = Option(findView(TR.profile_recycler))

  lazy val loadingView = Option(findView(TR.profile_loading))

  lazy val iconIndicatorDrawable = new PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  def initUi(implicit uiContext: UiContext[_], theme: NineCardsTheme): Ui[_] =
      (tabs <~ tlAddTabs(
        (getString(R.string.publications), PublicationsTab),
        (getString(R.string.subscriptions), SubscriptionsTab),
        (getString(R.string.accounts), AccountsTab))) ~
      (tabs <~ tlSetListener(this)) ~
      (recyclerView <~
        rvLayoutManager(new LinearLayoutManager(activityContextWrapper.application))) ~
      Ui(onProfileTabSelected(PublicationsTab))

  def userProfile(name: String, email: String, avatarUrl: String)(implicit contextWrapper: ContextWrapper, uiContext: UiContext[_]): Ui[_] =
    (userName <~ tvText(name)) ~
    (userEmail <~ tvText(email)) ~
      (userAvatar <~ ivUri(avatarUrl) <~ menuAvatarStyle)

  def handleToolbarVisibility(percentage: Float): Ui[_] = toolbar match {
    case Some(t) if percentage >= 0.5 && t.getVisibility == View.VISIBLE => toolbar <~ SnailsCommons.fadeOut()
    case Some(t) if percentage < 0.5 && t.getVisibility == View.INVISIBLE => toolbar <~ SnailsCommons.fadeIn()
    case _ => Ui.nop
  }

  def handleProfileVisibility(percentage: Float)(implicit context: ContextWrapper): Ui[_] = {
    val alpha = if (percentage <= 0.5f) 1f - (percentage * 2)  else 0f
    userContainer <~ vAlpha(alpha)
  }

  def showLoading: Ui[_] = (loadingView <~ vVisible) ~ (recyclerView <~ vInvisible)

  def showError(message: Int, clickAction: () => Unit): Ui[_] =
    (rootLayout <~ uiSnackbarIndefiniteAction(message, R.string.buttonErrorReload, clickAction)) ~
      (loadingView <~ vInvisible)

  def setPublicationsAdapter(items: Seq[String])
      (implicit uiContext: UiContext[_], theme: NineCardsTheme) =
    (recyclerView <~ vVisible <~ rvAdapter(new PublicationsAdapter(items))) ~
      (loadingView <~ vInvisible)

  def setSubscriptionsAdapter(items: Seq[String])
      (implicit uiContext: UiContext[_], theme: NineCardsTheme) =
    (recyclerView <~ vVisible <~ rvAdapter(new SubscriptionsAdapter(items))) ~
      (loadingView <~ vInvisible)

  def setAccountsAdapter(items: Seq[AccountSync])
      (implicit uiContext: UiContext[_], theme: NineCardsTheme) =
    (recyclerView <~ vVisible <~ rvAdapter(new AccountsAdapter(items))) ~
      (loadingView <~ vInvisible)

  override def onTabReselected(tab: Tab): Unit = {}

  override def onTabUnselected(tab: Tab): Unit = {}

  override def onTabSelected(tab: Tab): Unit = {
    tab.getTag match {
      case tab: ProfileTab => onProfileTabSelected(tab)
      case _ =>
    }
  }
}
