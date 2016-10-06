package cards.nine.services.shortcuts

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Shortcut

trait ShortcutsServices {

  /**
   * Get the applications that contains shortcuts to perform specific functions within an app
   * @return the Seq[cards.nine.models.Shortcut] contains
   *         information about shortcut for install it, get the icon, etc
   * @throws ShortcutServicesException if exist some problem to get the shortcuts in the cell phone
   */
  def getShortcuts(implicit context: ContextSupport): TaskService[Seq[Shortcut]]
}
