package cards.nine.process.theme

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.process.theme.models.NineCardsTheme

trait ThemeProcess {
  def getTheme(themeFile: String)(implicit context: ContextSupport): TaskService[NineCardsTheme]
}
