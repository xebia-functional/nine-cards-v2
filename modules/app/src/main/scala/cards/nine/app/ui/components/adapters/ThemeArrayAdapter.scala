/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.components.adapters

import android.text.TextUtils.TruncateAt
import android.view.{Gravity, View, ViewGroup}
import android.widget.{ArrayAdapter, FrameLayout, TextView}
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.app.ui.preferences.commons.FontSize
import cards.nine.commons.javaNull
import macroid.extras.UIActionsExtras._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.{DrawerBackgroundColor, DrawerIconColor, DrawerTextColor}
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

class ThemeArrayAdapter(icons: Seq[Int], values: Seq[String])(
    implicit contextWrapper: ContextWrapper,
    theme: NineCardsTheme)
    extends ArrayAdapter[String](contextWrapper.bestAvailable, 0, values.toArray) {

  val padding = resGetDimensionPixelSize(R.dimen.padding_large)

  override def getCount: Int = values.length

  override def getItemId(position: Int): Long = position

  override def getItem(position: Int): String =
    values lift position getOrElse javaNull

  override def getView(position: Int, convertView: View, parent: ViewGroup): View =
    createView(position)

  override def getDropDownView(position: Int, convertView: View, parent: ViewGroup): View =
    createView(position)

  private[this] def createView(position: Int): FrameLayout = {

    def commonStyle(position: Int) = {
      val textColor = theme.get(DrawerTextColor)
      val iconColor = theme.get(DrawerIconColor)
      val drawableTweak = icons lift position match {
        case Some(res) =>
          val drawable = resGetDrawable(res).colorize(iconColor)
          tvCompoundDrawablesWithIntrinsicBounds(left = Some(drawable)) + tvDrawablePadding(
            padding)
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
