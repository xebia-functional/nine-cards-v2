package cards.nine.app.ui.components.adapters

import android.text.TextUtils.TruncateAt
import android.view.{Gravity, View, ViewGroup}
import android.widget.{ArrayAdapter, FrameLayout, TextView}
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.app.ui.preferences.commons.FontSize
import cards.nine.commons.javaNull
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.{DrawerBackgroundColor, DrawerIconColor, DrawerTextColor}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

class ThemeArrayAdapter(icons: Seq[Int], values: Seq[String])(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
  extends ArrayAdapter[String](contextWrapper.bestAvailable, 0, values.toArray) {

  val padding = resGetDimensionPixelSize(R.dimen.padding_large)

  override def getCount: Int = values.length

  override def getItemId(position: Int): Long = position

  override def getItem(position: Int): String = values lift position getOrElse javaNull

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = createView(position)

  override def getDropDownView(position: Int, convertView: View, parent: ViewGroup): View = createView(position)

  private[this] def createView(position: Int): FrameLayout = {

    def commonStyle(position: Int) = {
      val textColor = theme.get(DrawerTextColor)
      val iconColor = theme.get(DrawerIconColor)
      val drawableTweak = icons lift position match {
        case Some(res) =>
          val drawable = resGetDrawable(res).colorize(iconColor)
          tvCompoundDrawablesWithIntrinsicBounds(left = Some(drawable)) + tvDrawablePadding(padding)
        case _ => Tweak.blank
      }
      vPaddings(padding) +
        vSelectableItemBackground +
        tvGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL) +
        drawableTweak +
        tvColor(textColor) +
        tvLines(1) +
        tvEllipsize(TruncateAt.END) +
        tvSizeResource(FontSize.getSizeResource) +
        tvText(values.lift(position) getOrElse "")
    }

    val backgroundColor = theme.get(DrawerBackgroundColor)
    (l[FrameLayout](
      w[TextView] <~ commonStyle(position)
    ) <~ vBackgroundColor(backgroundColor)).get
  }

}
