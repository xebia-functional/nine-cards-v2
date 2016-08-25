package com.fortysevendeg.ninecardslauncher.services.plus

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile

trait GooglePlusServices {

  /**
    * Load the user information for Google Plus
    *
    * @return the information about the profile
    */
  def loadUserProfile: CatsService[GooglePlusProfile]


}
