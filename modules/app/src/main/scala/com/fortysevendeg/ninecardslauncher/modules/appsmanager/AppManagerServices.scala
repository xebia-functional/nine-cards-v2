package com.fortysevendeg.ninecardslauncher.modules.appsmanager

import com.fortysevendeg.ninecardslauncher.utils.Service

trait AppManagerServices {

  def getApps: Service[GetAppsRequest, GetAppsResponse]

  def createBitmapsForNoPackagesInstalled: Service[IntentsRequest, PackagesResponse]

  def getCategorizedApps: Service[GetCategorizedAppsRequest, GetCategorizedAppsResponse]

  def getAppsByCategory: Service[GetAppsByCategoryRequest, GetAppsByCategoryResponse]

  def categorizeApps: Service[CategorizeAppsRequest, CategorizeAppsResponse]
}
