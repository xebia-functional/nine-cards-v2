package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.widget.ImageView
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ViewTweaks._
import macroid.{Tweak, ContextWrapper}

trait ProfileStyles {

  def menuAvatarStyle(implicit context: ContextWrapper): Tweak[ImageView] =
    Lollipop ifSupportedThen {
      vCircleOutlineProvider()
    } getOrElse Tweak.blank

}
