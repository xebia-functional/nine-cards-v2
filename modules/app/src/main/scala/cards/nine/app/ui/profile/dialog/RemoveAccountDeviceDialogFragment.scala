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

package cards.nine.app.ui.profile.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class RemoveAccountDeviceDialogFragment(resourceId: String, action: (String) => Unit)(
    implicit contextWrapper: ContextWrapper)
    extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    new AlertDialog.Builder(getActivity)
      .setMessage(R.string.removeAccountSyncMessage)
      .setPositiveButton(android.R.string.ok, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit =
          action(resourceId)
      })
      .setNegativeButton(android.R.string.cancel, javaNull)
      .create()
  }

}
