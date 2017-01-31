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

package cards.nine.app.ui.commons.dialogs.createoreditcollection

import android.app.{Activity, Dialog}
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.{LinearLayout, ScrollView}
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.models._
import cards.nine.models.types.ContactsCategory
import cards.nine.models.types.NineCardsCategory._
import cards.nine.models.types.NineCardsMoment._
import cards.nine.models.types.theme.{
  DrawerBackgroundColor,
  DrawerIconColor,
  DrawerTextColor,
  PrimaryColor
}
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class IconDialogFragment(
    iconSelected: String)(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
    extends DialogFragment
    with AppNineCardsIntentConversions {

  val categoryIcons = appsCategories map { cat =>
    val name =
      resGetString(cat.getStringResource).getOrElse(cat.getStringResource)
    ItemData(name, cat.getIconResource)
  } sortBy (_.name)

  val momentIcons = moments map { mom =>
    val name =
      resGetString(mom.getStringResource).getOrElse(mom.getStringResource)
    ItemData(name, mom.getIconResource)
  } sortBy (_.name)

  val contactIcon = Seq {
    val name = resGetString(ContactsCategory.getStringResource)
      .getOrElse(ContactsCategory.getStringResource)
    ItemData(name, ContactsCategory.getIconResource)
  }

  val icons = categoryIcons ++ momentIcons ++ contactIcon

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val rootView    = new ScrollView(getActivity)
    val contentView = new LinearLayout(getActivity)
    contentView.setOrientation(LinearLayout.VERTICAL)

    val views = icons map { ic =>
      new ItemView(ic, ic.icon == iconSelected)
    }

    ((rootView <~ vBackgroundColor(theme.get(DrawerBackgroundColor)) <~ vgAddView(contentView)) ~
      (contentView <~ vgAddViews(views))).run

    new AlertDialog.Builder(getActivity).setView(rootView).create()
  }

  class ItemView(data: ItemData, select: Boolean)
      extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.icon_info_item_dialog, this)

    lazy val text = Option(findView(TR.icon_dialog_name))
    lazy val icon = Option(findView(TR.icon_dialog_select))

    val primaryColor = theme.get(PrimaryColor)

    val colorizeDrawable =
      resGetDrawable(data.icon.getIconDetail).colorize(theme.get(DrawerIconColor))

    val drawable = PathMorphDrawable(
      defaultIcon = IconTypes.CHECK,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
      defaultColor = primaryColor)

    ((text <~
      tvColor(if (select) primaryColor else theme.get(DrawerTextColor)) <~
      tvText(data.name) <~
      tvCompoundDrawablesWithIntrinsicBounds(left = Some(colorizeDrawable))) ~
      (icon <~ (if (select) ivSrc(drawable) else Tweak.blank)) ~
      (this <~ On.click {
        Ui {
          val responseIntent = new Intent
          responseIntent.putExtra(CreateOrEditCollectionFragment.iconRequest, data.icon)
          getTargetFragment.onActivityResult(
            getTargetRequestCode,
            Activity.RESULT_OK,
            responseIntent)
          dismiss()
        }
      })).run

  }

  case class ItemData(name: String, icon: String)

}
