package cards.nine.app.ui.launcher.actions.createoreditcollection

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
import com.fortysevendeg.macroid.extras.ViewTweaks._
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.AppUtils._
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.process.theme.models.{DrawerBackgroundColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class ColorDialogFragment(index: Int)(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
  extends DialogFragment
  with AppNineCardsIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    def createRow(from: Int, to: Int): LinearLayout = {
      val layout = new LinearLayout(getActivity)
      layout.setOrientation(LinearLayout.HORIZONTAL)
      val params = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1)

      val views = from to to map (i => new ItemView(i, select = index == i))
      (layout <~ vgAddViews(views, params)).run
      layout
    }
    val rootView = new LinearLayout(getActivity)
    rootView.setOrientation(LinearLayout.VERTICAL)

    val views = Seq(createRow(0, 2), createRow(3, 5), createRow(6, 8))

    val params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    params.gravity = Gravity.CENTER

    (rootView <~ vBackgroundColor(theme.get(DrawerBackgroundColor)) <~ vgAddViews(views, params)).run

    new AlertDialog.Builder(getActivity).setView(rootView).create()
  }

  class ItemView(index: Int, select: Boolean)
    extends LinearLayout(contextWrapper.bestAvailable)
    with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.color_info_item_dialog, this)

    lazy val color = Option(findView(TR.color_info_image))

    val icon = PathMorphDrawable(
      defaultIcon = IconTypes.CHECK,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_large),
      defaultColor = resGetColor(R.color.color_selected_color_dialog),
      padding = resGetDimensionPixelSize(R.dimen.padding_large))

    ((color <~
      (if (select) ivSrc(icon) else Tweak.blank) <~
      vBackground(getDrawable(index))) ~
      (this <~
        On.click {
          Ui {
            val responseIntent = new Intent
            responseIntent.putExtra(CreateOrEditCollectionFragment.colorRequest, index)
            getTargetFragment.onActivityResult(getTargetRequestCode, Activity.RESULT_OK, responseIntent)
            dismiss()
          }
        })).run
  }

  private[this] def getDrawable(index: Int) = {
    val color = theme.getIndexColor(index)
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
