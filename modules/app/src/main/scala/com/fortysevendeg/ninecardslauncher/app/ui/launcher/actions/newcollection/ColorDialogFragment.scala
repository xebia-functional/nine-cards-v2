package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.newcollection

import android.app.{Activity, Dialog}
import android.content.Intent
import android.graphics.Paint.Style
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, LayoutInflater, View}
import android.widget.{ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper
import macroid.FullDsl._

case class ColorDialogFragment(index: Int)(implicit contextWrapper: ContextWrapper)
  extends DialogFragment
  with NineCardIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    def createRow(from: Int, to: Int): LinearLayout = {
      val layout = new LinearLayout(getActivity)
      layout.setOrientation(LinearLayout.HORIZONTAL)
      val params = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1)
      val views = from to to map (i => createViewItem(i, select = index == i))
      runUi(layout <~ vgAddViews(views, params))
      layout
    }
    val rootView = new LinearLayout(getActivity)
    rootView.setOrientation(LinearLayout.VERTICAL)

    val views = Seq(createRow(0, 2), createRow(3, 5), createRow(6, 8))

    val params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    params.gravity = Gravity.CENTER

    runUi(rootView <~ vgAddViews(views, params))

    new AlertDialog.Builder(getActivity).setView(rootView).create()
  }

  private[this] def createViewItem(index: Int, select: Boolean) = {
    val view = LayoutInflater.from(getActivity).inflate(R.layout.color_info_item_dialog, javaNull)
    view.findViewById(R.id.color_info_image) match {
      case i: ImageView =>
        if (select) {
          val icon = new PathMorphDrawable(
            defaultIcon = IconTypes.CHECK,
            defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_large),
            defaultColor = resGetColor(R.color.color_selected_color_dialog),
            padding = resGetDimensionPixelSize(R.dimen.padding_large))
          i.setImageDrawable(icon)
        }
        i.setBackground(getDrawable(index))
    }
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        val responseIntent = new Intent
        responseIntent.putExtra(NewCollectionFragment.colorRequest, index)
        getTargetFragment.onActivityResult(getTargetRequestCode, Activity.RESULT_OK, responseIntent)
        dismiss()
      }
    })
    view
  }

  private[this] def getDrawable(index: Int) = {
    val color = resGetColor(getIndexColor(index))
    val size = resGetDimensionPixelSize(R.dimen.size_icon_select_new_collection)
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.setIntrinsicHeight(size)
    drawable.setIntrinsicWidth(size)
    drawable.getPaint.setColor(color)
    drawable.getPaint.setStyle(Style.FILL)
    drawable.getPaint.setAntiAlias(true)
    drawable
  }

}
