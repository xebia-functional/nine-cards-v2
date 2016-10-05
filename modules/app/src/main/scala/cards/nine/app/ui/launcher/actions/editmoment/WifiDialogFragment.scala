package cards.nine.app.ui.launcher.actions.editmoment

import android.app.{Activity, Dialog}
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.{LinearLayout, ScrollView}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import cards.nine.app.commons.AppNineCardIntentConversions
import cards.nine.app.ui.commons.ops.ColorOps._
import cards.nine.process.theme.models._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class WifiDialogFragment(wifis: Seq[String])(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
  extends DialogFragment
  with AppNineCardIntentConversions {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val rootView = new ScrollView(getActivity)
    val contentView = new LinearLayout(getActivity)
    contentView.setOrientation(LinearLayout.VERTICAL)

    val views = wifis map (new ItemView(_))

    ((rootView <~ vBackgroundColor(theme.get(DrawerBackgroundColor)) <~ vgAddView(contentView)) ~
      (contentView <~ vgAddViews(views))).run

    new AlertDialog.Builder(getActivity).setView(rootView).create()
  }

  class ItemView(wifi: String)
    extends LinearLayout(contextWrapper.bestAvailable)
    with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.icon_info_item_dialog, this)

    lazy val text = Option(findView(TR.icon_dialog_name))
    lazy val icon = Option(findView(TR.icon_dialog_select))

    val primaryColor = theme.get(PrimaryColor)

    val colorizeDrawable = resGetDrawable(R.drawable.icon_edit_moment_wifi).colorize(theme.get(DrawerIconColor))

    ((text <~
      tvColor(theme.get(DrawerTextColor)) <~
      tvText(wifi) <~
      tvCompoundDrawablesWithIntrinsicBounds(left = Some(colorizeDrawable))) ~
      (this <~ On.click{
        Ui {
          val responseIntent = new Intent
          responseIntent.putExtra(EditMomentFragment.wifiRequest, wifi)
          getTargetFragment.onActivityResult(getTargetRequestCode, Activity.RESULT_OK, responseIntent)
          dismiss()
        }
      })
    ).run

  }

}
