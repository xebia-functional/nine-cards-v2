package cards.nine.app.ui.components.adapters

import android.view.{Gravity, View, ViewGroup}
import android.widget.{ArrayAdapter, TextView}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.process.theme.models.{DrawerBackgroundColor, DrawerIconColor, DrawerTextColor, NineCardsTheme}
import cards.nine.commons.javaNull
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

class ThemeArrayAdapter(icons: Seq[Int], values: Seq[String])(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
  extends ArrayAdapter[String](contextWrapper.bestAvailable, 0, values.toArray) {

  val padding = resGetDimensionPixelSize(R.dimen.padding_default)

  override def getCount: Int = values.length

  override def getItemId(position: Int): Long = position

  override def getItem(position: Int): String = values lift position getOrElse javaNull

  override def getView(position: Int, convertView: View, parent: ViewGroup): View =
    (w[TextView] <~ commonStyle(position)).get

  override def getDropDownView(position: Int, convertView: View, parent: ViewGroup): View =
    (w[TextView] <~ commonStyle(position)).get

  private[this] def commonStyle(position: Int) = {
    val textColor = theme.get(DrawerTextColor)
    val iconColor = theme.get(DrawerIconColor)
    val backgroundColor = theme.get(DrawerBackgroundColor)
    val drawableTweak = icons lift position match {
      case Some(res) =>
        val drawable = resGetDrawable(res).colorize(iconColor)
        tvCompoundDrawablesWithIntrinsicBounds(left = Some(drawable)) + tvDrawablePadding(padding)
      case _ => Tweak.blank
    }
    vPaddings(padding) +
      tvGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL) +
      drawableTweak +
      tvColor(textColor) +
      tvSizeResource(R.dimen.text_large) +
      tvText(values.lift(position) getOrElse "") +
      vBackgroundColor(backgroundColor)
  }

}
