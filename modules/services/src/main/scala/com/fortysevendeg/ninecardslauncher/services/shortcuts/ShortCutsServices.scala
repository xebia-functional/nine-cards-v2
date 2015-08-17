package com.fortysevendeg.ninecardslauncher.services.shortcuts

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.ShortCut

trait ShortCutsServices {
  def getShortCuts(implicit context: ContextSupport): ServiceDef2[Seq[ShortCut], ShortCutException]
}
