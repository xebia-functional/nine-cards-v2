package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.services.apps.AppsInstalledException
import com.fortysevendeg.ninecardslauncher.services.image.BitmapTransformationException
import com.fortysevendeg.ninecardslauncher.services.persistence.RepositoryException

trait DeviceProcess {
  def getCategorizedApps(implicit context: ContextSupport): ServiceDef2[Seq[AppCategorized], RepositoryException with AppsInstalledException with BitmapTransformationException]
  def categorizeApps(implicit context: ContextSupport):  ServiceDef2[Unit, AppsInstalledException with BitmapTransformationException with NineCardsException with RepositoryException]
  def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport): ServiceDef2[Unit, BitmapTransformationException with NineCardsException]
}
