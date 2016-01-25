package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.support.v4.app.{FragmentManager, Fragment}
import android.support.v7.app.AppCompatActivity
import android.widget.{TextView, ImageView}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}
import macroid.{FragmentManagerContext, ActivityContextWrapper, Ui}
import macroid.FullDsl._
import macroid._

trait ProfileComposer
  extends ProfileStyles {

  self: AppCompatActivity with TypedFindView with SystemBarsTint =>

  lazy val rootLayout = Option(findView(TR.profile_root))

  lazy val barLayout = Option(findView(TR.profile_appbar))

  lazy val collapsingBar = Option(findView(TR.profile_collapsing))

  lazy val toolbar = Option(findView(TR.profile_toolbar))

  lazy val userContainer = Option(findView(TR.profile_user_container))

  lazy val userAvatar: Option[ImageView] = Option(findView(TR.profile_user_avatar))

  lazy val userName: Option[TextView] = Option(findView(TR.profile_user_name))

  def initUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme, managerContext: FragmentManagerContext[Fragment, FragmentManager]): Ui[_] =
    (rootLayout <~ dlStatusBarBackground(android.R.color.transparent)) ~
    (userAvatar <~ menuAvatarStyle)

  def userProfile(avatarUrl: String, name: String)(implicit uiContext: UiContext[_]): Ui[_] =
    (userName <~ tvText(name)) ~
      (userAvatar <~ ivUri(avatarUrl))

}
