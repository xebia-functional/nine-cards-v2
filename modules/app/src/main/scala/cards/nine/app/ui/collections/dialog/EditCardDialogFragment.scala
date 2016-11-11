package cards.nine.app.ui.collections.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.{OnClickListener, OnShowListener}
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import macroid.extras.TextViewTweaks._
import cards.nine.app.ui.commons.CommonsExcerpt._
import macroid.extras.EditTextTweaks._
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid._

class EditCardDialogFragment(cardName: String, onChangeName: (Option[String]) => Unit)(implicit contextWrapper: ContextWrapper)
  extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    val dialogView = new DialogView

    val dialog = new AlertDialog.Builder(getActivity).
      setTitle(R.string.editCardDialogTitle).
      setView(dialogView).
      setPositiveButton(android.R.string.ok, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit = {
          onChangeName(dialogView.readText)
        }
      }).
      setNegativeButton(android.R.string.cancel, javaNull).
      create()

    dialog.setOnShowListener(new OnShowListener {
      override def onShow(dialog: DialogInterface): Unit = dialogView.showKeyboard.run
    })

    dialogView.setCardName(cardName).run
    dialog
  }

  class DialogView
    extends LinearLayout(contextWrapper.bestAvailable)
    with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.dialog_edit_card, this)

    private[this] lazy val editCardName = findView(TR.dialog_edit_card_name)

    def setCardName(cardName: String): Ui[Any] = editCardName <~ tvText(cardName)

    def readText: Option[String] = (editCardName ~> text).get

    def showKeyboard: Ui[Any] = editCardName <~ etShowKeyboard

  }

}
