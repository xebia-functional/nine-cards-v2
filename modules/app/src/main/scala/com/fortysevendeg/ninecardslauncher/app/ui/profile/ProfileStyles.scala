package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.support.v7.widget.CardView
import android.view.View
import android.widget.{ImageView, TextView}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CardBackgroundColor, CardTextColor, DrawerTextColor, NineCardsTheme}
import macroid.{ContextWrapper, Tweak}

trait ProfileStyles {

  def menuAvatarStyle(implicit context: ContextWrapper): Tweak[ImageView] =
    Lollipop ifSupportedThen {
      vCircleOutlineProvider()
    } getOrElse Tweak.blank

}

trait SubscriptionsAdapterStyles {

  implicit val theme: NineCardsTheme

  val themeTextColor = theme.get(CardTextColor)

  def rootStyle()(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[View] =
    Tweak[View] { view =>

    }

  def textStyle(implicit context: ContextWrapper): Tweak[TextView] =
    tvColor(themeTextColor)

}

trait EmptyProfileAdapterStyles {

  implicit val theme: NineCardsTheme

  val themeTextColor = theme.get(CardTextColor)

  def rootStyle()(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[CardView] =
    vVisible +
      cvCardBackgroundColor(theme.get(CardBackgroundColor))

  def textStyle(implicit context: ContextWrapper): Tweak[TextView] =
    tvColor(theme.get(DrawerTextColor).alpha(0.8f))

}