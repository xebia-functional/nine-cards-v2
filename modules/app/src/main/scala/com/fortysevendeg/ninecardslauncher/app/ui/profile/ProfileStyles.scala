package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.view.View
import android.widget.{TextView, ImageView}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CardTextColor, NineCardsTheme}
import macroid.{Tweak, ContextWrapper}

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