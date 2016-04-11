package com.fortysevendeg.ninecardslauncher.app.ui.profile.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

class RemoveAccountDeviceDialogFragment(onClickListener: () => Unit)(implicit contextWrapper: ContextWrapper)
  extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    new AlertDialog.Builder(getActivity).
      setMessage(R.string.removeAccountSyncMessage).
      setPositiveButton(android.R.string.ok, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit = {
          onClickListener()
        }
      }).
      setNegativeButton(android.R.string.cancel, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit = {
          dismiss()
        }
      }).
      create()
  }

}
