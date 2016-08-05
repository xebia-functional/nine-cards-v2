package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.{ColorDrawable, Drawable, RippleDrawable, StateListDrawable}
import android.support.v7.widget.CardView
import android.widget.Button
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorOps._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import macroid.{ContextWrapper, Tweak}

trait RecommendationsAdapterStyles {

  def cardRootStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[CardView] =
    cvCardBackgroundColor(theme.get(CollectionDetailCardBackgroundColor))

  def installNowStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[Button] =
    tvColor(theme.get(CollectionDetailTextCardColor)) + vBackground(createBackground)

  private[this] def createBackground(implicit context: ContextWrapper, theme: NineCardsTheme): Drawable = {
    val alphaDefault = .1f
    val color = theme.get(CollectionDetailCardBackgroundPressedColor)
    Lollipop ifSupportedThen {
      new RippleDrawable(
        new ColorStateList(Array(Array()), Array(color)),
        javaNull,
        new ColorDrawable(Color.BLACK.alpha(alphaDefault)))
    } getOrElse {
      val states = new StateListDrawable()
      states.addState(Array[Int](android.R.attr.state_pressed), new ColorDrawable(color.alpha(alphaDefault)))
      states.addState(Array.emptyIntArray, new ColorDrawable(Color.TRANSPARENT))
      states
    }
  }

}

