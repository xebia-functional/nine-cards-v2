package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.view.View
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import macroid.{Tweak, ContextWrapper}

trait ProfileStyles {

  def menuAvatarStyle(implicit context: ContextWrapper): Tweak[ImageView] =
    Lollipop ifSupportedThen {
      vCircleOutlineProvider()
    } getOrElse Tweak.blank

}

trait PublicationsAdapterStyles {

  def rootStyle()(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[View] =
    Tweak[View] { view =>

    }

}

trait SubscriptionsAdapterStyles {

  def rootStyle()(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[View] =
    Tweak[View] { view =>

    }

}

trait AccountsAdapterStyles {

  def rootStyle()(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[View] =
    Tweak[View] { view =>

    }

}