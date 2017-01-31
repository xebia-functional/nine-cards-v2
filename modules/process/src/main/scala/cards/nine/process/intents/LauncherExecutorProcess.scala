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

package cards.nine.process.intents

import cards.nine.commons.contexts.ActivityContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.NineCardsIntent

trait LauncherExecutorProcess {

  /**
   * Executes a NineCardIntent
   *
   * @param intent the intent
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception or
   *         an IntentLauncherServicesPermissionException if this exception is a SecurityException
   */
  def execute(intent: NineCardsIntent)(
      implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch the contact preview based on a lookup key
   *
   * @param contactLookupKey the lookup key
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def executeContact(contactLookupKey: String)(
      implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch a share intent with the title and text specified
   *
   * @param text the text
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchShare(text: String)(
      implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch the search intent
   *
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchSearch(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch the Google weather intent
   *
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchGoogleWeather(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch the voice search intent
   *
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchVoiceSearch(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch the settings for a specific application
   *
   * @param packageName the application package
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchSettings(packageName: String)(
      implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch the intent for uninstall for a specific application
   *
   * @param packageName the application package
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchUninstall(packageName: String)(
      implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch the phone dial
   *
   * @param phoneNumber an optional phone number that will be sent to the dial activity
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchDial(phoneNumber: Option[String] = None)(
      implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch the intent for the Google Play Store
   *
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchPlayStore(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch a specific application
   *
   * @param packageName the application package
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchApp(packageName: String)(
      implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch the Google Play Store view for a specific application
   *
   * @param packageName the application package
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchGooglePlay(packageName: String)(
      implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
   * Launch the intent for an url
   *
   * @param url the web url
   * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
   */
  def launchUrl(url: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

}
