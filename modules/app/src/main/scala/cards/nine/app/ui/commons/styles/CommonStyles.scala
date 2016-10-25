package cards.nine.app.ui.commons.styles

import android.widget.TextView
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.DrawerTextColor
import com.fortysevendeg.macroid.extras.TextTweaks._
import macroid.{ContextWrapper, Tweak}

trait CommonStyles {

  val titleAlpha = 0.87f

  val subtitleAlpha = 0.54f

  def titleTextStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(DrawerTextColor).alpha(titleAlpha))

  def subtitleTextStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(DrawerTextColor).alpha(subtitleAlpha))
}
