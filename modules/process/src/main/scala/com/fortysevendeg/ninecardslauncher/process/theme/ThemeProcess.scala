package com.fortysevendeg.ninecardslauncher.process.theme

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme

trait ThemeProcess {
  def getTheme(themeFile: String)(implicit context: ContextSupport): TaskService[NineCardsTheme]
}
