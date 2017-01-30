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

class EditAccountDeviceDialogFragment(
    title: Int,
    maybeText: Option[String],
    action: (Option[String] => Unit))(implicit contextWrapper: ContextWrapper)
    extends DialogFragment {

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

    val dialogView = new DialogView

    val dialog = new AlertDialog.Builder(getActivity)
      .setTitle(title)
      .setView(dialogView)
      .setPositiveButton(android.R.string.ok, new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit =
          action(dialogView.readText.get)
      })
      .setNegativeButton(android.R.string.cancel, javaNull)
      .create()

    dialog.setOnShowListener(new OnShowListener {
      override def onShow(dialog: DialogInterface): Unit = {
        (dialogView.showKeyboard ~
          (maybeText map dialogView.setText getOrElse Ui.nop)).run
      }
    })

    dialog
  }

  class DialogView extends LinearLayout(contextWrapper.bestAvailable) with TypedFindView {

    LayoutInflater.from(getActivity).inflate(R.layout.dialog_edit_text, this)

    private[this] lazy val editText = findView(TR.dialog_edittext)

    def setText(text: String) = editText <~ tvText(text)

    def readText: Ui[Option[String]] = editText ~> text

    def showKeyboard: Ui[Any] = editText <~ etShowKeyboard

  }

}
