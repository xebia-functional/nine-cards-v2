package cards.nine.services.calls

import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Call

trait CallsServices {

  /**
   * Get the last calls in the system
   *
   * @return the Seq[cards.nine.models.Call] contains information about the widget
   * @throws CallsServicesPermissionException if the permission for read calls hasn't been granted
   * @throws CallsServicesException if exist some problem to get the calls in the cell phone
   */
  def getLastCalls: TaskService[Seq[Call]]
}
