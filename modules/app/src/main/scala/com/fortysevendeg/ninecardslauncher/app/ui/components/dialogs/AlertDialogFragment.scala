package com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

class AlertDialogFragment(
  message: Int,
  okMessage: Int = android.R.string.ok,
  cancelMessage: Int = android.R.string.cancel,
  positiveAction: () => Unit = () => (),
  negativeAction: () => Unit = () => ())
  extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    new AlertDialog.Builder(getActivity).
      setMessage(message).
      setPositiveButton(okMessage, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit = {
          positiveAction()
          dismiss()
        }
      }).
      setNegativeButton(cancelMessage, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit = {
          negativeAction()
          dismiss()
        }
      }).
      create()
  }

}