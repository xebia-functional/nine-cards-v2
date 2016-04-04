package com.fortysevendeg.ninecardslauncher.app.ui.launcher.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

class RemoveCollectionDialogFragment(collection: Collection)(implicit contextWrapper: ContextWrapper, presenter: LauncherPresenter)
  extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    new AlertDialog.Builder(getActivity).
      setMessage(R.string.removeCollectionMessage).
      setPositiveButton(android.R.string.ok, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit = {
          presenter.removeCollection(collection).run
          dismiss()
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
