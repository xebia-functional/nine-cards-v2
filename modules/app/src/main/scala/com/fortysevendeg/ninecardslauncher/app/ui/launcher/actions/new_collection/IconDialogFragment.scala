package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.new_collection

import android.app.{Activity, Dialog}
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View}
import android.widget.{ImageView, LinearLayout, ScrollView, TextView}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ColorsUtils, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.ui.components.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper
import macroid.FullDsl._

case class IconDialogFragment(categorySelected: String)(implicit contextWrapper: ContextWrapper)
  extends DialogFragment
  with NineCardIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val rootView = new ScrollView(getActivity)
    val contentView = new LinearLayout(getActivity)
    contentView.setOrientation(LinearLayout.VERTICAL)

    val views = categories map (cat => createViewItem(cat, select = cat == categorySelected))

    runUi(
      (rootView <~ vgAddView(contentView)) ~
        (contentView <~ vgAddViews(views)))

    new AlertDialog.Builder(getActivity).setView(rootView).create()
  }

  private[this] def createViewItem(category: String, select: Boolean) = {
    val name = resGetString(category.toLowerCase).getOrElse(category.toLowerCase)
    val view = LayoutInflater.from(getActivity).inflate(R.layout.icon_info_item_dialog, null)
    view.findViewById(R.id.icon_dialog_name) match {
      case t: TextView =>
        if (select) t.setTextColor(R.color.text_selected_color_dialog)
        t.setText(name)
        val colorizeDrawable = ColorsUtils.colorizeDrawable(resGetDrawable(iconCollectionDetail(category)), Color.GRAY)
        t.setCompoundDrawablesWithIntrinsicBounds(colorizeDrawable, null, null, null)
    }
    if (select) {
      view.findViewById(R.id.icon_dialog_select) match {
        case i: ImageView =>
          val icon = new PathMorphDrawable(
            defaultIcon = IconTypes.CHECK,
            defaultStroke = resGetDimensionPixelSize(R.dimen.default_stroke),
            defaultColor = resGetColor(R.color.text_selected_color_dialog))
          i.setImageDrawable(icon)
      }
    }
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        val responseIntent = new Intent
        responseIntent.putExtra(NewCollectionFragment.iconRequest, category)
        getTargetFragment.onActivityResult(getTargetRequestCode, Activity.RESULT_OK, responseIntent)
        dismiss()
      }
    })
    view
  }

}
