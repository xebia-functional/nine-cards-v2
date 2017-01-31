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

package cards.nine.app.ui.commons.dialogs

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.DrawerTabsBackgroundColor
import com.fortysevendeg.ninecardslauncher.R
import macroid.extras.FloatingActionButtonTweaks._
import macroid.extras.ImageViewTweaks._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.{ContextWrapper, Tweak}

trait Styles {

  def recyclerStyle(implicit context: ContextWrapper): Tweak[RecyclerView] =
    rvFixedSize

  def fabButtonMenuStyle(color: Int)(
      implicit context: ContextWrapper): Tweak[FloatingActionButton] = {
    val iconFabButton = PathMorphDrawable(
      defaultIcon = IconTypes.CHECK,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default))
    val darkColor = color.dark()
    ivSrc(iconFabButton) +
      fbaColor(color, darkColor)
  }

  def scrollableStyle(color: Int)(implicit context: ContextWrapper, theme: NineCardsTheme) =
    fslColor(color, theme.get(DrawerTabsBackgroundColor))

}
