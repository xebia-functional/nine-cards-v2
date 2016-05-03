package com.fortysevendeg.ninecardslauncher.process.social

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2

trait SocialProfileProcess {

  /**
    * Load the user information for Google Plus and updates the values on the database
    */
  def updateUserProfile()(implicit context: ContextSupport): ServiceDef2[Unit, SocialProfileProcessException]

}
