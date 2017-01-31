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

package cards.nine.services.shortcuts

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Shortcut

trait ShortcutsServices {

  /**
   * Get the applications that contains shortcuts to perform specific functions within an app
   * @return the Seq[cards.nine.models.Shortcut] contains
   *         information about shortcut for install it, get the icon, etc
   * @throws ShortcutServicesException if exist some problem to get the shortcuts in the cell phone
   */
  def getShortcuts(implicit context: ContextSupport): TaskService[Seq[Shortcut]]
}
