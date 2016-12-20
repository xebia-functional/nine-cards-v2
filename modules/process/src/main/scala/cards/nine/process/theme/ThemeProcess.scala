package cards.nine.process.theme

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.NineCardsTheme

trait ThemeProcess {

  /**
   * Gets the teme
   *
   * @param themeFile with the name of the file
   * @return the cards.nine.process.theme.models.NineCardsTheme
   */
  def getTheme(themeFile: String)(implicit context: ContextSupport): TaskService[NineCardsTheme]
}
