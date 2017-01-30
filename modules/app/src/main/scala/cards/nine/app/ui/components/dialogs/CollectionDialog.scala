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

package cards.nine.app.ui.components.dialogs

import android.app.Dialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.commons._
import cards.nine.models.types.theme.{DrawerBackgroundColor, DrawerIconColor, DrawerTextColor}
import cards.nine.models.{Collection, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import macroid.extras.ImageViewTweaks._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

class CollectionDialog(
    moments: Seq[Collection],
    onCollection: (Int) => Unit,
    onDismissDialog: () => Unit)(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
    extends BottomSheetDialogFragment
    with TypedFindView { dialog =>

  lazy val selectCollectionList = findView(TR.select_collection_list)

  var rootView: Option[ViewGroup] = None

  override protected def findViewById(id: Int): View =
    rootView.map(_.findViewById(id)).orNull

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    val baseView = LayoutInflater
      .from(getActivity)
      .inflate(R.layout.select_collection_dialog, javaNull, false)
      .asInstanceOf[ViewGroup]
    rootView = Option(baseView)
    val views = moments map (moment => new CollectionItem(moment))
    (selectCollectionList <~
      vBackgroundColor(theme.get(DrawerBackgroundColor)) <~
      vgAddViews(views)).run
    dialog.setContentView(baseView)
  }

  class CollectionItem(collection: Collection)
      extends LinearLayout(contextWrapper.getOriginal)
      with TypedFindView {

    LayoutInflater.from(getContext).inflate(TR.layout.select_collection_item, this)

    val icon = findView(TR.select_collection_item_icon)

    val text = findView(TR.select_collection_item_text)

    ((this <~ On.click(Ui {
      onCollection(collection.id)
      dialog.dismiss()
    })) ~
      (icon <~ ivSrc(collection.getIconDetail) <~ tivDefaultColor(theme.get(DrawerIconColor))) ~
      (text <~ tvText(collection.name) <~ tvColor(theme.get(DrawerTextColor)))).run

  }

}
