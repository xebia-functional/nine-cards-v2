package com.fortysevendeg.ninecardslauncher.services.apps

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

trait AppsServices {
  def getInstalledApps(implicit context: ContextSupport): ServiceDef2[Seq[Application], AppsInstalledException]
}
