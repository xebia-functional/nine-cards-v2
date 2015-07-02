package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

import scala.concurrent.Future


trait DeviceProcess {
  def getApps(request: GetAppsRequest)(implicit context: ContextSupport): Future[GetAppsResponse]
  def getCategorizedApps(request: GetCategorizedAppsRequest)(implicit context: ContextSupport): Future[GetCategorizedAppsResponse]
  def getAppsByCategory(request: GetAppsByCategoryRequest)(implicit context: ContextSupport): Future[GetAppsByCategoryResponse]
  def categorizeApps(request: CategorizeAppsRequest)(implicit context: ContextSupport): Future[CategorizeAppsResponse]
}
