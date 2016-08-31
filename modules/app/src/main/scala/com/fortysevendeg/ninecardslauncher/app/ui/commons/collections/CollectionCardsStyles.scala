package com.fortysevendeg.ninecardslauncher.app.ui.commons.collections

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.{ColorDrawable, Drawable, RippleDrawable, StateListDrawable}
import android.support.v7.widget.CardView
import android.widget.{Button, TextView}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import macroid.{ContextWrapper, Tweak}

trait CollectionCardsStyles {

  implicit val theme: NineCardsTheme

  val themeTextColor = theme.get(CardTextColor)

  def cardRootStyle(implicit context: ContextWrapper): Tweak[CardView] =
    cvCardBackgroundColor(theme.get(CardBackgroundColor))

  def textStyle(implicit context: ContextWrapper): Tweak[TextView] =
    tvColor(themeTextColor)

  def buttonStyle(implicit context: ContextWrapper): Tweak[Button] =
    tvColor(themeTextColor) + vBackground(createBackground)

  def leftDrawableTextStyle(resourceId: Int)(implicit context: ContextWrapper): Tweak[TextView] =
    tvColor(themeTextColor) + tvCompoundDrawablesWithIntrinsicBounds(left = Some(tintDrawable(resourceId)))

  def tintDrawable(resourceId: Int)(implicit context: ContextWrapper): Drawable =
    resGetDrawable(resourceId).colorize(theme.get(DrawerIconColor))

  private[this] def createBackground(implicit context: ContextWrapper): Drawable = {
    val alphaDefault = .1f
    val color = theme.get(CardBackgroundPressedColor)
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

