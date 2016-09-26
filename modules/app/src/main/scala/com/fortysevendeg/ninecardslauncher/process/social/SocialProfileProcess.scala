package com.fortysevendeg.ninecardslauncher.process.social

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.google.GoogleServiceClient
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService

trait SocialProfileProcess {

  /**
    * Creates the social profile API client
    * The ActivityContextSupport should have an activity of type SocialProfileClientListener
    * @param clientId the OAuth Client Id for requesting the token Id
    * @param account the email for the client
    * @return the GoogleAPIClient
    */
  def createSocialProfileClient(
    clientId: String,
    account: String)(implicit contextSupport: ContextSupport): TaskService[GoogleServiceClient]

  /**
    * Load the user information for Google Plus and updates the values on the database
    * @param client the google API client
    * @return the profile name
    */
  def updateUserProfile(client: GoogleServiceClient)(implicit context: ContextSupport): TaskService[Option[String]]

}