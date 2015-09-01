package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.device.models.{Shortcut, AppCategorized, Contact}

trait DeviceProcess {
  def getCategorizedApps(implicit context: ContextSupport): ServiceDef2[Seq[AppCategorized], AppCategorizationException]
  def categorizeApps(implicit context: ContextSupport):  ServiceDef2[Unit, AppCategorizationException]
  def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport): ServiceDef2[Unit, CreateBitmapException]

  /**
   * Get the available applications that contain shortcuts creating Intents and Drawables necessaries for UI actions
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut] contains
   *         information about shortcut with the Intents and Drawables for UI actions
   * @throws ShortcutException if exist some problem to get the shortcuts in the cell phone
   */
  def getAvailableShortcuts(implicit context: ContextSupport): ServiceDef2[Seq[Shortcut], ShortcutException]

  def getFavoriteContacts(implicit context: ContextSupport): ServiceDef2[Seq[Contact], ContactException]
}
