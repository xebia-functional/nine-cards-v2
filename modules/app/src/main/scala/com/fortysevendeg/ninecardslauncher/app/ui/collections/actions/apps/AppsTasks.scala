package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.device.models.{IterableApps, TermCounter}
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, GetAppOrder}

trait AppsTasks {

  def getLoadApps(order: GetAppOrder)(implicit context: ContextSupport, di: Injector): ServiceDef2[(IterableApps, Seq[TermCounter]), AppException] =
    for {
      iterableApps <- di.deviceProcess.getIterableApps(order)
      counters <- di.deviceProcess.getTermCountersForApps(order)
    } yield (iterableApps, counters)

  def getLoadAppsByCategory(category: NineCardCategory)
    (implicit context: ContextSupport, di: Injector): ServiceDef2[(IterableApps, Seq[TermCounter]), AppException] =
    for {
      iterableApps <- di.deviceProcess.getIterableAppsByCategory(category.name)
    } yield (iterableApps, Seq.empty)

}
