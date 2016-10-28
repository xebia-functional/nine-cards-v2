package cards.nine.app.ui.commons.styles

import android.widget.TextView
import cards.nine.app.ui.components.widgets.TintableImageView
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.DrawerTextColor
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ContextWrapper, Tweak}

trait CommonStyles {

  val titleAlpha = 0.87f

  val subtitleAlpha = 0.54f

  def iconMomentStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    tivColor(theme.get(DrawerTextColor))

  def titleTextStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(DrawerTextColor).alpha(titleAlpha))

  def subtitleTextStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(DrawerTextColor).alpha(subtitleAlpha))

  protected def getStarDrawable(value: Double): Int = value match {
    case v if v < 1.1 => R.drawable.recommendations_starts_01
    case v if v < 1.6 => R.drawable.recommendations_starts_01_5
    case v if v < 2.1 => R.drawable.recommendations_starts_02
    case v if v < 2.6 => R.drawable.recommendations_starts_02_5
    case v if v < 3.1 => R.drawable.recommendations_starts_03
    case v if v < 3.6 => R.drawable.recommendations_starts_03_5
    case v if v < 4.1 => R.drawable.recommendations_starts_04
    case v if v < 4.6 => R.drawable.recommendations_starts_04_5
    case _ => R.drawable.recommendations_starts_05
  }

}
