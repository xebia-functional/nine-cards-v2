package com.fortysevendeg.ninecardslauncher.services.calls

import cards.nine.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.services.calls.models.Call

trait CallsServices {
  /**
   * Get the last calls in the system
   *
   * @return the Seq[com.fortysevendeg.ninecardslauncher.services.calls.models.Call] contains
   *         information about the widget
   * @throws CallsServicesPermissionException if the permission for read calls hasn't been granted
   * @throws CallsServicesException if exist some problem to get the calls in the cell phone
   */
  def getLastCalls: TaskService[Seq[Call]]
}
