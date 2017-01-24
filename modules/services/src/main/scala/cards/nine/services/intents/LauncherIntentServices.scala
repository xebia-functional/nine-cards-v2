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

package cards.nine.services.intents

import android.content.Intent
import cards.nine.commons.contexts.ActivityContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.IntentAction

trait LauncherIntentServices {

  /**
   * This method try to execute an intent based on the provided action
   * @param intentAction specifies the intent to be executed
   * @return a TaskService[Unit] that will contain an Unit if the intent has been executed successfully
   *         an IntentLauncherServicesPermissionException if there are insufficient permissions to execute the intent or
   *         an IntentLauncherServicesException if the service can't access to the activity or the execution throw a different exception
   */
  def launchIntentAction(intentAction: IntentAction)(
      implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * This method try to execute directly an intent
   * @param intent the intent to be executed
   * @return a TaskService[Unit] that will contain an Unit if the intent has been executed successfully
   *         an IntentLauncherServicesPermissionException if there are insufficient permissions to execute the intent or
   *         an IntentLauncherServicesException if the service can't access to the activity or the execution throw a different exception
   */
  def launchIntent(intent: Intent)(
      implicit activityContext: ActivityContextSupport): TaskService[Unit]

}
