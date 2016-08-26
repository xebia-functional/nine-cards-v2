package com.fortysevendeg.ninecardslauncher.services.widgets

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.services.widgets.models.Widget

trait WidgetsServices {
  /**
   * Get the available widgets in the system
   * @return the Seq[com.fortysevendeg.ninecardslauncher.services.widgets.models.Widget] contains
   *         information about the widget
   * @throws WidgetServicesException if exist some problem to get the widgets in the cell phone
   */
  def getWidgets(implicit context: ContextSupport): TaskService[Seq[Widget]]
}
