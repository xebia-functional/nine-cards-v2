package com.fortysevendeg.ninecardslauncher.services.apps

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

import scala.concurrent.Future

trait AppsServices {
  def getInstalledApps(request: GetInstalledAppsRequest)(implicit context: ContextSupport): Future[GetInstalledAppsResponse]
}
