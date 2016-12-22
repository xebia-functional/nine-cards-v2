package cards.nine.app.ui.components.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.{Gravity, LayoutInflater}
import android.widget.{LinearLayout, ScrollView, TextView}
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.models._
import cards.nine.models.types.theme.{DrawerBackgroundColor, DrawerIconColor, DrawerTextColor}
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

case class BluetoothDialogFragment(devices: Seq[String], onSelected: (String) => Unit)(
    implicit contextWrapper: ContextWrapper,
    theme: NineCardsTheme)
    extends DialogFragment
    with AppNineCardsIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val rootView    = new ScrollView(getActivity)
    val contentView = new LinearLayout(getActivity)
    contentView.setOrientation(LinearLayout.VERTICAL)

    val views = if (devices.isEmpty) {
      Seq(
        (w[TextView] <~
          vMatchWidth <~
          tvGravity(Gravity.CENTER) <~
          vPaddings(resGetDimensionPixelSize(R.dimen.padding_large)) <~
          tvText(R.string.bluetoothDisconnected) <~
          tvColor(theme.get(DrawerTextColor)) <~
          tvSizeResource(R.dimen.text_default)).get)
    } else {
      devices map (new ItemView(_))
    }

    ((rootView <~ vBackgroundColor(theme.get(DrawerBackgroundColor)) <~ vgAddView(contentView)) ~
      (contentView <~ vgAddViews(views))).run

    new AlertDialog.Builder(getActivity).setView(rootView).create()
  }

  class ItemView(devices: String)
      extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.icon_info_item_dialog, this)

    lazy val text = Option(findView(TR.icon_dialog_name))
    lazy val icon = Option(findView(TR.icon_dialog_select))

    val colorizeDrawable =
      resGetDrawable(R.drawable.icon_edit_moment_bluetooth).colorize(theme.get(DrawerIconColor))

    ((text <~
      tvColor(theme.get(DrawerTextColor)) <~
      tvText(devices) <~
      tvCompoundDrawablesWithIntrinsicBounds(left = Some(colorizeDrawable))) ~
      (this <~ On.click {
        Ui {
          onSelected(devices)
          dismiss()
        }
      })).run

  }

}
