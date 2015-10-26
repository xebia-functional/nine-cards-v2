package com.fortysevendeg.ninecardslauncher.app.receivers

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

trait AppBroadcastReceiverTasks {

  def addApp(packageName: String)(implicit di: Injector, contextSupport: ContextSupport) =
    for {
      _ <- di.deviceProcess.saveApp(packageName)
      _ <- di.collectionProcess.updateNoInstalledCardsInCollections(packageName)
    } yield (())

  def deleteApp(packageName: String)(implicit di: Injector, contextSupport: ContextSupport) =
    di.deviceProcess.deleteApp(packageName)

  def updateApp(packageName: String)(implicit di: Injector, contextSupport: ContextSupport) =
    di.deviceProcess.updateApp(packageName)

}
