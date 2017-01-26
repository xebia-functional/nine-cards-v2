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

package cards.nine.app.ui.collections.dialog.publishcollection

import android.view.{View, ViewGroup}
import android.widget.TextView
import cards.nine.app.ui.commons.styles.CommonStyles
import cards.nine.app.ui.components.widgets.TintableImageView
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.{CardBackgroundColor, DrawerIconColor}
import macroid.extras.ImageViewTweaks._
import macroid.extras.LinearLayoutTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak}

trait PublishCollectionStyles extends CommonStyles {

  def dialogBackgroundStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[View] =
    vBackgroundColor(theme.get(CardBackgroundColor))

  def iconStyle(
      alpha: Float = 1f)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[View] =
    vBackgroundColor(theme.get(DrawerIconColor).alpha(alpha))

  def paginationItemStyle(implicit context: ContextWrapper): Tweak[TintableImageView] = {
    val size   = resGetDimensionPixelSize(R.dimen.publish_collection_size_pager)
    val margin = resGetDimensionPixelSize(R.dimen.publish_collection_margin_pager)
    lp[ViewGroup](size, size) +
      llLayoutMargin(margin, margin, margin, margin) +
      ivSrc(R.drawable.publish_collection_wizard_pager)
  }

  def spinnerStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    titleTextStyle +
      tvSizeResource(R.dimen.text_xlarge) +
      tvColor(theme.get(DrawerIconColor).alpha(0.3f))

}
