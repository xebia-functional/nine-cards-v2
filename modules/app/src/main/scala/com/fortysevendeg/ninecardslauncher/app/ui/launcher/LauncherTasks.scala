package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.DockAppException
import com.fortysevendeg.ninecardslauncher.process.device.models.DockApp

trait LauncherTasks {

  def getLauncherApps(implicit context: ContextSupport, di: Injector): ServiceDef2[(Seq[Collection], Seq[DockApp]), CollectionException with DockAppException] =
    for {
      collections <- di.collectionProcess.getCollections
      dockApps <- di.deviceProcess.getDockApps
    } yield (collections, dockApps)

}
