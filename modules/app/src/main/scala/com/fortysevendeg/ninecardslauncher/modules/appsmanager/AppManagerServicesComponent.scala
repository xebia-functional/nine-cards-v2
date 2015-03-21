package com.fortysevendeg.ninecardslauncher.modules.appsmanager

import com.fortysevendeg.ninecardslauncher.commons.Service

trait AppManagerServices {
  def getApps: Service[GetAppsRequest, GetAppsResponse]
}

trait AppManagerServicesComponent {
  val appManagerServices: AppManagerServices
}