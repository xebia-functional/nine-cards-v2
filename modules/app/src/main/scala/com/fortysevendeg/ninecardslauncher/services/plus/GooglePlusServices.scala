package com.fortysevendeg.ninecardslauncher.services.plus

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile
import com.google.android.gms.common.api.GoogleApiClient

trait GooglePlusServices {

  /**
    * Creates the Google Plus API client
    * @param clientId the OAuth Client Id for requesting the token Id
    * @param account the email for the client
    * @return the GoogleAPIClient
    */
  def createGooglePlusClient(
    clientId: String,
    account: String)(implicit contextSupport: ContextSupport): TaskService[GoogleApiClient]

  /**
    * Load the user information for Google Plus
    * @param client the google API client
    * @return the information about the profile
    */
  def loadUserProfile(client: GoogleApiClient): TaskService[GooglePlusProfile]


}
