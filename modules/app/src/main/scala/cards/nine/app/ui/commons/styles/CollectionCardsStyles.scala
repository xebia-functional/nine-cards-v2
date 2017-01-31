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

  def leftDrawableTextStyle(
      resourceId: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(CardTextColor)) + tvCompoundDrawablesWithIntrinsicBounds(
      left = Some(tintDrawable(resourceId)))

  def tintDrawable(
      resourceId: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Drawable =
    resGetDrawable(resourceId).colorize(theme.get(DrawerIconColor))

}
