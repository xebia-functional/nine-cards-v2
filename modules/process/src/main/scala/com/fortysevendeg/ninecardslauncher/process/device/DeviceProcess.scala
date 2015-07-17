package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized

import scalaz.\/
import scalaz.concurrent.Task

trait DeviceProcess {
  def getCategorizedApps(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppCategorized]]
  def categorizeApps(implicit context: ContextSupport):  Task[NineCardsException \/ Unit]
  def createBitmapsForNoPackagesInstalled(packages: Seq[String])(implicit context: ContextSupport):  Task[NineCardsException \/ Unit]
}
