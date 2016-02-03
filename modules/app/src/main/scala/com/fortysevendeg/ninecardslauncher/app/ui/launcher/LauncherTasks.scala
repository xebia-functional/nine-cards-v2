package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.models.{DockApp, IterableApps, IterableContacts, TermCounter}
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, ContactException, ContactsFilter, DockAppException, GetAppOrder}

trait LauncherTasks {

  def getLauncherApps(implicit context: ContextSupport, di: Injector): ServiceDef2[(Seq[Collection], Seq[DockApp]), CollectionException with DockAppException] =
    for {
      collections <- di.collectionProcess.getCollections
      dockApps <- di.deviceProcess.getDockApps
    } yield (collections, dockApps)

  def getLoadApps(order: GetAppOrder)(implicit context: ContextSupport, di: Injector): ServiceDef2[(IterableApps, Seq[TermCounter]), AppException] =
    for {
      iterableApps <- di.deviceProcess.getIterableApps(order)
      counters <- di.deviceProcess.getTermCountersForApps(order)
    } yield (iterableApps, counters)

  def getLoadContacts(order: ContactsFilter)(implicit context: ContextSupport, di: Injector): ServiceDef2[(IterableContacts, Seq[TermCounter]), ContactException] =
    for {
      iterableContacts <- di.deviceProcess.getIterableContacts(order)
      counters <- di.deviceProcess.getTermCountersForContacts(order)
    } yield (iterableContacts, counters)

}
