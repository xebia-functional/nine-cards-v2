package cards.nine.services.widgets

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Widget

trait WidgetsServices {
  /**
   * Get the available widgets in the system
   * @return the Seq[cards.nine.services.widgets.models.Widget] contains
   *         information about the widget
   * @throws WidgetServicesException if exist some problem to get the widgets in the cell phone
   */
  def getWidgets(implicit context: ContextSupport): TaskService[Seq[Widget]]
}
