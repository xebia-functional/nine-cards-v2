package com.fortysevendeg.ninecardslauncher.services.plus

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile
import com.google.android.gms.common.api.GoogleApiClient

trait GooglePlusServices {

  /**
    * Load the user information for Google Plus
    * @param client the google API client
    * @return the information about the profile
    */
  def loadUserProfile(client: GoogleApiClient): TaskService[GooglePlusProfile]


}
