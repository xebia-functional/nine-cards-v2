package cards.nine.app.ui.commons.styles

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.{ColorDrawable, Drawable, RippleDrawable, StateListDrawable}
import android.widget.TextView
import cards.nine.app.ui.components.widgets.TintableImageView
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.{CardBackgroundPressedColor, DrawerTextColor}
import macroid.extras.DeviceVersion.Lollipop
import macroid.extras.TextViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ContextWrapper, Tweak}

trait CommonStyles {

  val titleAlpha = 0.87f

  val subtitleAlpha = 0.54f

  val alphaDefault = .1f

  def iconMomentStyle(
      implicit context: ContextWrapper,
      theme: NineCardsTheme): Tweak[TintableImageView] =
    tivColor(theme.get(DrawerTextColor))

  def titleTextStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(DrawerTextColor).alpha(titleAlpha))

  def subtitleTextStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(DrawerTextColor).alpha(subtitleAlpha))

  def createBackground(implicit context: ContextWrapper, theme: NineCardsTheme): Drawable = {

    @SuppressLint(Array("NewApi"))
    def createRippleDrawable(color: Int) =
      new RippleDrawable(
        new ColorStateList(Array(Array()), Array(color)),
        javaNull,
        new ColorDrawable(Color.BLACK.alpha(alphaDefault)))

    @SuppressLint(Array("NewApi"))
    def createStateListDrawable(color: Int) = {
      val states = new StateListDrawable()
      states.addState(
        Array[Int](android.R.attr.state_pressed),
        new ColorDrawable(color.alpha(alphaDefault)))
      states.addState(Array.emptyIntArray, new ColorDrawable(Color.TRANSPARENT))
      states
    }

    val color = theme.get(CardBackgroundPressedColor)
    Lollipop ifSupportedThen createRippleDrawable(color) getOrElse createStateListDrawable(color)
  }

  protected def getStarDrawable(value: Double): Int = value match {
    case v if v < 1.1 => R.drawable.recommendations_starts_01
    case v if v < 1.6 => R.drawable.recommendations_starts_01_5
    case v if v < 2.1 => R.drawable.recommendations_starts_02
    case v if v < 2.6 => R.drawable.recommendations_starts_02_5
    case v if v < 3.1 => R.drawable.recommendations_starts_03
    case v if v < 3.6 => R.drawable.recommendations_starts_03_5
    case v if v < 4.1 => R.drawable.recommendations_starts_04
    case v if v < 4.6 => R.drawable.recommendations_starts_04_5
    case _            => R.drawable.recommendations_starts_05
  }

}
