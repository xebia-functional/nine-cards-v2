package com.fortysevendeg.ninecardslauncher.services.apps

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

trait AppsServices {

  /**
   * Obtains a sequence with all the installed apps
    *
    * @throws AppsInstalledException if exist some problem obtaining the installed apps
   */
  def getInstalledApplications(implicit context: ContextSupport): TaskService[Seq[Application]]

  /**
   * Obtains an installed app by the package name
    *
    * @param packageName the package name of the app to get
   * @throws AppsInstalledException if exist some problem obtaining the installed app
   */
  def getApplication(packageName: String)(implicit context: ContextSupport): TaskService[Application]

  /**
    * Return a sequence with the default apps for ten predefined actions
    *
    * @return Sequence of `Application` with the data of the apps
    * @throws AppsInstalledException if there was an error with trying to get the default apps
    */
  def getDefaultApps(implicit context: ContextSupport): TaskService[Seq[Application]]
}
