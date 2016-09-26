package com.fortysevendeg.ninecardslauncher.services.plus

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.google.GoogleServiceClient
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile

trait GooglePlusServices {

  /**
    * Creates the Google Plus API client
    * @param clientId the OAuth Client Id for requesting the token Id
    * @param account the email for the client
    * @return the GoogleServiceClient
    */
  def createGooglePlusClient(
    clientId: String,
    account: String)(implicit contextSupport: ContextSupport): TaskService[GoogleServiceClient]

  /**
    * Load the user information for Google Plus
    * @param client the google API client
    * @return the information about the profile
    */
  def loadUserProfile(client: GoogleServiceClient): TaskService[GooglePlusProfile]


}
