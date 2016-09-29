package com.fortysevendeg.ninecardslauncher.process.theme

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme

trait ThemeProcess {
  def getTheme(themeFile: String)(implicit context: ContextSupport): TaskService[NineCardsTheme]
}
