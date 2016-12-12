package cards.nine.app.ui.commons.styles

import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.widget.{Button, TextView}
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models._
import cards.nine.models.types.theme._
import macroid.extras.CardViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
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

}

