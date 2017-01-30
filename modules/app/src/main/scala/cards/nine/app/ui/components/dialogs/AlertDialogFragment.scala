/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.components.dialogs

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
    negativeAction: () => Unit = () => (),
    showCancelButton: Boolean = true)
    extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    val alert = new AlertDialog.Builder(getActivity)
      .setMessage(message)
      .setPositiveButton(okMessage, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit = {
          positiveAction()
          dismiss()
        }
      })
    if (showCancelButton) {
      alert.setNegativeButton(cancelMessage, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit = {
          negativeAction()
          dismiss()
        }
      })
    }
    alert.create()
  }

}
