package com.fortysevendeg.ninecardslauncher.process.accounts

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService

trait UserAccountsProcess {

  /**
    * Load the user information for Google Plus and updates the values on the database
    *
    * @return the profile name
    */
  def updateUserProfile()(implicit context: ContextSupport): CatsService[Option[String]]

}
