package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.newcollection

import android.app.{Activity, Dialog}
import android.content.Intent
import android.graphics.Paint.Style
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, LayoutInflater}
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak, Ui}

case class ColorDialogFragment(index: Int)(implicit contextWrapper: ContextWrapper)
  extends DialogFragment
  with NineCardIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    def createRow(from: Int, to: Int): LinearLayout = {
      val layout = new LinearLayout(getActivity)
      layout.setOrientation(LinearLayout.HORIZONTAL)
      val params = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1)

      val views = from to to map { i =>
        new ItemView(i, select = index == i)
      }
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

  class ItemView(index: Int, select: Boolean)
    extends LinearLayout(contextWrapper.bestAvailable)
    with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.color_info_item_dialog, this)

    lazy val color = Option(findView(TR.color_info_image))

    val icon = new PathMorphDrawable(
      defaultIcon = IconTypes.CHECK,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_large),
      defaultColor = resGetColor(R.color.color_selected_color_dialog),
      padding = resGetDimensionPixelSize(R.dimen.padding_large))

    runUi(
      color <~
        (if (select) ivSrc(icon) else Tweak.blank) <~
        ivSrc(getDrawable(index))  <~
        On.click{
          Ui {
            val responseIntent = new Intent
            responseIntent.putExtra(NewCollectionFragment.iconRequest, index)
            getTargetFragment.onActivityResult(getTargetRequestCode, Activity.RESULT_OK, responseIntent)
            dismiss()
          }
        }
    )
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
