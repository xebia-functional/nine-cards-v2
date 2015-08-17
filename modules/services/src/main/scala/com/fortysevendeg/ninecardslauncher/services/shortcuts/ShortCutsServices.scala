package com.fortysevendeg.ninecardslauncher.services.shortcuts

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.ShortCut

trait ShortCutsServices {

  /**
   * Get the applications that contains shortcuts to perform specific functions within an app
   * @return the Seq[com.fortysevendeg.ninecardslauncher.services.shortcuts.models.ShortCut] contains
   *         information about shortcut for install it, get the icon, etc
   * @throws ShortCutServicesException if exist some problem to get the shortcuts in the cell phone
   */
  def getShortCuts(implicit context: ContextSupport): ServiceDef2[Seq[ShortCut], ShortCutServicesException]
}
