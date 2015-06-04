package com.fortysevendeg.ninecardslauncher.modules.appsmanager

import com.fortysevendeg.ninecardslauncher.commons.Service

trait AppManagerServices {
  def createBitmapsForNoPackagesInstalled: Service[IntentsRequest, PackagesResponse]
}

trait AppManagerServicesComponent {
  val appManagerServices: AppManagerServices
}