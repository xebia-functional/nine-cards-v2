package com.fortysevendeg.ninecardslauncher.app.ui.profile.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.profile.ProfilePresenter
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

class CopyAccountDeviceDialogFragment(resourceId: String)(implicit contextWrapper: ContextWrapper, profilePresenter: ProfilePresenter)
  extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    val dialogView = new DialogView(R.string.copyAccountSyncMessage)

    new AlertDialog.Builder(getActivity).
      setTitle(R.string.copyAccountSyncDialogTitle).
      setView(dialogView).
      setPositiveButton(android.R.string.ok, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit = {
          dialogView.editText foreach { editText =>
            profilePresenter.copyDevice(editText.getText.toString, resourceId)
          }
        }
      }).
      setNegativeButton(android.R.string.cancel, javaNull).
      create()
  }

  class DialogView(message: Int)
    extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.dialog_edit_text, this)

    lazy val textView = Option(findView(TR.dialog_message))

    lazy val editText = Option(findView(TR.dialog_edittext))

    (textView <~ tvText(message)).run

  }

}
