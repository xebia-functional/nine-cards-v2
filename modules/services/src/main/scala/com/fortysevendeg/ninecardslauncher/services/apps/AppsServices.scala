package com.fortysevendeg.ninecardslauncher.services.apps

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

trait AppsServices {

  /**
   * Obtains a sequence with all the installed apps
   * @throws AppsInstalledException if exist some problem obtaining the installed apps
   */
  def getInstalledApplications(implicit context: ContextSupport): ServiceDef2[Seq[Application], AppsInstalledException]

  /**
   * Obtains an installed app by the package name
   * @param packageName the package name of the app to get
   * @throws AppsInstalledException if exist some problem obtaining the installed app
   */
  def getApplication(packageName: String)(implicit context: ContextSupport): ServiceDef2[Application, AppsInstalledException]
}
