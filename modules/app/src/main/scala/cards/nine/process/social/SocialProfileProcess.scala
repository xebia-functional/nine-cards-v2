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

package cards.nine.process.social

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

trait SocialProfileProcess {

  /**
   * Creates the social profile API client
   * The ActivityContextSupport should have an activity of type SocialProfileClientListener
   * @param clientId the OAuth Client Id for requesting the token Id
   * @param account the email for the client
   * @return the GoogleAPIClient
   */
  def createSocialProfileClient(clientId: String, account: String)(
      implicit contextSupport: ContextSupport): TaskService[GoogleApiClient]

  /**
   * Load the user information for Google Plus and updates the values on the database
   * @param client the google API client
   * @return the profile name
   */
  def updateUserProfile(client: GoogleApiClient)(
      implicit context: ContextSupport): TaskService[Option[String]]

}

trait SocialProfileClientListener {

  def onPlusConnectionSuspended(cause: Int): Unit

  def onPlusConnected(): Unit

  def onPlusConnectionFailed(connectionResult: ConnectionResult): Unit

}
