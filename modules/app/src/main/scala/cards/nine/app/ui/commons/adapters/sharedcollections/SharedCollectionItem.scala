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

package cards.nine.app.ui.commons.adapters.sharedcollections

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.{View, ViewGroup}
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.SharedCollectionOps._
import cards.nine.app.ui.commons.styles.{CollectionCardsStyles, CommonStyles}
import cards.nine.models.types._
import cards.nine.models.{NineCardsTheme, SharedCollection, SharedCollectionPackage}
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait SharedCollectionItem extends CollectionCardsStyles with CommonStyles with TypedFindView {

  implicit val context: ActivityContextWrapper

  implicit val uiContext: UiContext[_]

  def content: ViewGroup

  lazy val root = findView(TR.public_collections_item_layout)

  lazy val iconContent = findView(TR.public_collections_item_content)

  lazy val icon = findView(TR.public_collections_item_icon)

  lazy val name = findView(TR.public_collections_item_name)

  lazy val author = findView(TR.public_collections_item_author)

  lazy val downloads = findView(TR.public_collections_item_downloads)

  lazy val subscriptions = findView(TR.public_collections_item_subscriptions)

  lazy val appsIcons = findView(TR.public_collections_item_apps)

  lazy val addCollection = findView(TR.public_collections_item_add_collection)

  lazy val shareCollection = findView(TR.public_collections_item_share_collection)

  lazy val line = findView(TR.public_collections_item_line)

  lazy val background = new ShapeDrawable(new OvalShape)

  def initialize()(implicit theme: NineCardsTheme): Ui[Any] = {
    (root <~ cardRootStyle) ~
      (iconContent <~ vBackground(background)) ~
      (name <~ titleTextStyle) ~
      (line <~ vBackgroundColor(theme.getLineColor)) ~
      (author <~ subtitleTextStyle) ~
      (downloads <~ leftDrawableTextStyle(R.drawable.icon_collection_downloads) <~ subtitleTextStyle) ~
      (subscriptions <~ leftDrawableTextStyle(R.drawable.icon_collection_subscriptions) <~ subtitleTextStyle) ~
      (addCollection <~ buttonStyle) ~
      (shareCollection <~ ivSrc(tintDrawable(R.drawable.icon_dialog_collection_share)))
  }

  def bind(collection: SharedCollection, onAddCollection: => Unit, onShareCollection: => Unit)(
      implicit theme: NineCardsTheme): Ui[Any] = {

    def addCollectionTweak() = collection.locallyAdded match {
      case Some(true) =>
        tvText(R.string.alreadyAddedCollection) +
          tvAllCaps(false) + tvItalicLight + vEnabled(false)
      case _ =>
        tvText(R.string.addMyCollection) +
          tvAllCaps(true) + tvNormalMedium + vEnabled(true)
    }

    background.getPaint.setColor(theme.getRandomIndexColor)
    val apps = collection.resolvedPackages
    (icon <~ ivSrc(collection.getIconCollectionDetail)) ~
      (appsIcons <~
        vgRemoveAllViews <~
        fblAddItems(apps, (item: SharedCollectionPackage) => {
          ivUri(item.icon)
        })) ~
      (name <~ tvText(collection.name)) ~
      (author <~ tvText(collection.author)) ~
      (subscriptions <~
        (collection.subscriptions match {
          case Some(number) =>
            vVisible + tvText(resGetString(R.string.subscriptions_number, number.toString))
          case _ => vGone
        })) ~
      (downloads <~ tvText(s"${collection.views}")) ~
      (addCollection <~ addCollectionTweak() <~ On.click(
        (addCollection <~ vEnabled(false)) ~ Ui(onAddCollection))) ~
      (shareCollection <~ On.click(Ui(onShareCollection)))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}
