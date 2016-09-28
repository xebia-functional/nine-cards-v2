package com.fortysevendeg.ninecardslauncher.app.ui.commons.styles

import android.widget.TextView
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{DrawerTextColor, NineCardsTheme}
import macroid.{ContextWrapper, Tweak}

trait CommonStyles {

  val titleAlpha = 0.87f

  val subtitleAlpha = 0.54f

  def titleTextStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(DrawerTextColor).alpha(titleAlpha))

  def subtitleTextStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(DrawerTextColor).alpha(subtitleAlpha))
}
