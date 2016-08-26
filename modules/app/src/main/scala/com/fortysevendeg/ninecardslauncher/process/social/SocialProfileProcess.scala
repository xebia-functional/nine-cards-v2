package com.fortysevendeg.ninecardslauncher.process.social

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService

trait SocialProfileProcess {

  /**
    * Load the user information for Google Plus and updates the values on the database
    *
    * @return the profile name
    */
  def updateUserProfile()(implicit context: ContextSupport): TaskService[Option[String]]

}
