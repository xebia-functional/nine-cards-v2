package cards.nine.app.ui.commons.dialogs.shortcuts

import android.app.Dialog
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.models.Shortcut
import com.fortysevendeg.ninecardslauncher.R

class ShortcutDialogFragment
  extends BaseActionFragment
  with ShortcutDialogUiActions
  with ShortcutDialogDOM
  with ShortcutsUiListener
  with AppNineCardsIntentConversions { self =>

  lazy val shortcutJobs = new ShortcutDialogJobs(self)

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    shortcutJobs.initialize().resolveAsyncServiceOr(_ => shortcutJobs.showErrorLoadingShortcuts())
  }

  override def loadShortcuts(): Unit =
    shortcutJobs.loadShortcuts().resolveAsyncServiceOr(_ => shortcutJobs.showErrorLoadingShortcuts())

  def onConfigure(shortcut: Shortcut): Unit = shortcutJobs.configureShortcut(shortcut).resolveAsync()
}
