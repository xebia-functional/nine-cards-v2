package com.fortysevendeg.ninecardslauncher.services.apps

import macroid.ContextWrapper

import scala.concurrent.Future

trait AppsServices {
  def getInstalledApps(request: GetInstalledAppsRequest)(implicit context: ContextWrapper): Future[GetInstalledAppsResponse]
}
