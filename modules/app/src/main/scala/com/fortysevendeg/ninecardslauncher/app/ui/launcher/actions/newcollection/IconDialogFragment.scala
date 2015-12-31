package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.newcollection

import android.app.{Activity, Dialog}
import android.content.{Context, Intent}
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View}
import android.widget.{LinearLayout, ScrollView}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.ContextWrapper
import macroid.FullDsl._

case class IconDialogFragment(categorySelected: String)(implicit contextWrapper: ContextWrapper)
  extends DialogFragment
  with NineCardIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val rootView = new ScrollView(getActivity)
    val contentView = new LinearLayout(getActivity)
    contentView.setOrientation(LinearLayout.VERTICAL)

    val views = appsCategories map {cat =>
      val view = new ItemView(contextWrapper.bestAvailable)
      view.populate(cat, select = cat == categorySelected)
      view
    }

    runUi(
      (rootView <~ vgAddView(contentView)) ~
        (contentView <~ vgAddViews(views)))

    new AlertDialog.Builder(getActivity).setView(rootView).create()
  }

  class ItemView(context: Context)
    extends LinearLayout(context)
    with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.icon_info_item_dialog, this)

    lazy val text = findView(TR.icon_dialog_name)
    lazy val icon = findView(TR.icon_dialog_select)

    def populate(category: NineCardCategory, select: Boolean) = {
      val name = resGetString(category.getStringResource).getOrElse(category.getStringResource)

      if (select) text.setTextColor(R.color.text_selected_color_dialog)
      text.setText(name)
      val colorizeDrawable = ColorsUtils.colorizeDrawable(resGetDrawable(iconCollectionDetail(category.name)), Color.GRAY)
      text.setCompoundDrawablesWithIntrinsicBounds(colorizeDrawable, javaNull, javaNull, javaNull)

      if (select) {
        val drawable = new PathMorphDrawable(
          defaultIcon = IconTypes.CHECK,
          defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
          defaultColor = resGetColor(R.color.text_selected_color_dialog))
        icon.setImageDrawable(drawable)
      }

      setOnClickListener(new OnClickListener {
        override def onClick(v: View): Unit = {
          val responseIntent = new Intent
          responseIntent.putExtra(NewCollectionFragment.iconRequest, category.name)
          getTargetFragment.onActivityResult(getTargetRequestCode, Activity.RESULT_OK, responseIntent)
          dismiss()
        }
      })
    }


  }

}
