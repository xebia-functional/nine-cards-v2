package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.new_collection

import android.app.{Activity, Dialog}
import android.os.Bundle
import android.content.Intent
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.{LinearLayout, ScrollView, TextView}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper
import macroid.FullDsl._

case class IconDialogFragment(implicit contextWrapper: ContextWrapper)
  extends DialogFragment
  with NineCardIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val rootView = new ScrollView(getActivity)
    val contentView = new LinearLayout(getActivity)
    contentView.setOrientation(LinearLayout.VERTICAL)

    val views = categories map (cat => createViewItem(cat, select = false))

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
        t.setText(name)
        t.setCompoundDrawablesWithIntrinsicBounds(iconCollectionDetail(category), 0, 0, 0)
    }
    // TODO Select item image
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
