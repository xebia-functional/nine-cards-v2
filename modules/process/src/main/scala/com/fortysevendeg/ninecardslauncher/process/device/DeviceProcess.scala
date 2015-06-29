package com.fortysevendeg.ninecardslauncher.process.device

import macroid.ContextWrapper

import scala.concurrent.Future


trait DeviceProcess {
  def getApps(request: GetAppsRequest)(implicit context: ContextWrapper): Future[GetAppsResponse]
  def getCategorizedApps(request: GetCategorizedAppsRequest)(implicit context: ContextWrapper): Future[GetCategorizedAppsResponse]
  def getAppsByCategory(request: GetAppsByCategoryRequest)(implicit context: ContextWrapper): Future[GetAppsByCategoryResponse]
  def categorizeApps(request: CategorizeAppsRequest)(implicit context: ContextWrapper): Future[CategorizeAppsResponse]
}
