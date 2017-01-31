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

package cards.nine.app.ui.commons.dialogs.publicollections

import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.{LinearLayout, TextView}
import cards.nine.models.{Collection, SharedCollection}
import cards.nine.models.types.{NineCardsCategory, TypeSharedCollection}
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.Contexts
import macroid.FullDsl._

trait PublicCollectionsDOM { self: TypedFindView with Contexts[Fragment] =>

  lazy val recycler = findView(TR.actions_recycler)

  var typeFilter = slot[TextView]

  var categoryFilter = slot[TextView]

  lazy val tabsMenu = l[LinearLayout](
    w[TextView] <~
      wire(typeFilter) <~
      tabButtonStyle(R.string.top),
    w[TextView] <~
      wire(categoryFilter) <~
      tabButtonStyle(R.string.communication)).get

  def tabButtonStyle(text: Int) = {
    val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)
    val paddingLarge   = resGetDimensionPixelSize(R.dimen.padding_large)
    val paddingSmall   = resGetDimensionPixelSize(R.dimen.padding_small)
    vWrapContent +
      tvText(text) +
      tvNormalMedium +
      tvSizeResource(R.dimen.text_large) +
      tvGravity(Gravity.CENTER_VERTICAL) +
      tvColorResource(R.color.tab_public_collection_dialog) +
      vPadding(
        paddingTop = paddingDefault,
        paddingBottom = paddingDefault,
        paddingRight = paddingLarge) +
      tvDrawablePadding(paddingSmall) +
      tvCompoundDrawablesWithIntrinsicBoundsResources(right = R.drawable.tab_menu_indicator)
  }

}

trait PublicCollectionsListener {

  def loadPublicCollectionsByTypeSharedCollection(typeSharedCollection: TypeSharedCollection): Unit

  def loadPublicCollectionsByCategory(category: NineCardsCategory): Unit

  def loadPublicCollections(): Unit

  def onAddCollection(sharedCollection: SharedCollection): Unit

  def onShareCollection(sharedCollection: SharedCollection): Unit

}
