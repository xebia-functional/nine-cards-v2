package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized

trait DeviceProcess {
  def getCategorizedApps(implicit context: ContextSupport): ServiceDef2[Seq[AppCategorized], AppCategorizationException]
  def categorizeApps(implicit context: ContextSupport):  ServiceDef2[Unit, AppCategorizationException]
  def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport): ServiceDef2[Unit, CreateBitmapException]
}
