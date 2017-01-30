/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
