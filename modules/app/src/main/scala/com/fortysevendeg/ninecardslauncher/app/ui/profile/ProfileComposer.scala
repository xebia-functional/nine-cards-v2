package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.PathMorphDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._
import macroid.FullDsl._

trait ProfileComposer
  extends ProfileStyles {

  self: AppCompatActivity with TypedFindView with SystemBarsTint with Contexts[AppCompatActivity] =>

  lazy val barLayout = Option(findView(TR.profile_appbar))

  lazy val toolbar = Option(findView(TR.profile_toolbar))

  lazy val userContainer = Option(findView(TR.profile_user_container))

  lazy val userAvatar = Option(findView(TR.profile_user_avatar))

  lazy val userName = Option(findView(TR.profile_user_name))

  lazy val tabs = Option(findView(TR.profile_tabs))

  lazy val iconIndicatorDrawable = new PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  def initUi: Ui[_] =
    (userAvatar <~ menuAvatarStyle) ~
      (tabs <~ tlAddTabs(
        getString(R.string.publications),
        getString(R.string.subscriptions),
        getString(R.string.accounts)))

  def userProfile(name: String, avatarUrl: String)(implicit uiContext: UiContext[_]): Ui[_] =
    (userName <~ tvText(name)) ~
      (userAvatar <~ ivUri(avatarUrl))

  def handleToolbarVisibility(percentage: Float)(implicit context: ContextWrapper): Ui[_] = toolbar match {
    case Some(t) if percentage >= 0.5 && t.getVisibility == View.VISIBLE => toolbar <~ SnailsCommons.fadeOut(Some(100))
    case Some(t) if percentage < 0.5 && t.getVisibility == View.INVISIBLE => toolbar <~ SnailsCommons.fadeIn(Some(100))
    case _ => Ui.nop
  }

  def handleProfileVisibility(percentage: Float)(implicit context: ContextWrapper): Ui[_] = userContainer match {
    case Some(c) if percentage <= 0.5f => Ui(c.setAlpha(1f - (percentage * 2)))
    case Some(c) if percentage > 0.5f => Ui(c.setAlpha(0f))
    case _ => Ui.nop
  }


}
