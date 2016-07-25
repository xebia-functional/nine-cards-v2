package com.fortysevendeg.ninecardslauncher.app.ui.profile.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.{OnClickListener, OnShowListener}
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.fortysevendeg.ninecardslauncher.app.ui.profile.ProfilePresenter
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsExcerpt._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import macroid._

class CopyAccountDeviceDialogFragment(cloudId: String)(implicit contextWrapper: ContextWrapper, profilePresenter: ProfilePresenter)
  extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    val dialogView = new DialogView

    val dialog = new AlertDialog.Builder(getActivity).
      setTitle(R.string.copyAccountSyncDialogTitle).
      setView(dialogView).
      setPositiveButton(android.R.string.ok, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit =
          profilePresenter.copyDevice(dialogView.readText.get, cloudId)
      }).
      setNegativeButton(android.R.string.cancel, javaNull).
      create()

    dialog.setOnShowListener(new OnShowListener {
      override def onShow(dialog: DialogInterface): Unit = dialogView.showKeyboard.run
    })

    dialog
  }

  class DialogView
    extends LinearLayout(contextWrapper.bestAvailable)
      with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.dialog_edit_text, this)

    private[this] lazy val editText = Option(findView(TR.dialog_edittext))

    def readText: Ui[Option[String]] = (editText ~> text) map (_.flatten)

    def showKeyboard: Ui[Any] = editText <~ etShowKeyboard

  }

}
