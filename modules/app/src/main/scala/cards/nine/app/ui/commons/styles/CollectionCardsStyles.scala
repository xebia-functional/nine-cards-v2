package cards.nine.app.ui.commons.styles

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
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons._
import cards.nine.process.theme.models._
import macroid.{ContextWrapper, Tweak}

trait CollectionCardsStyles extends CommonStyles {

  def cardRootStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[CardView] =
    cvCardBackgroundColor(theme.get(CardBackgroundColor))

  def textStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(CardTextColor))

  def buttonStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[Button] =
    tvColor(theme.get(DrawerTextColor).alpha(subtitleAlpha)) + vBackground(createBackground)

  def leftDrawableTextStyle(resourceId: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(CardTextColor)) + tvCompoundDrawablesWithIntrinsicBounds(left = Some(tintDrawable(resourceId)))

  def tintDrawable(resourceId: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Drawable =
    resGetDrawable(resourceId).colorize(theme.get(DrawerIconColor))

  private[this] def createBackground(implicit context: ContextWrapper, theme: NineCardsTheme): Drawable = {
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

