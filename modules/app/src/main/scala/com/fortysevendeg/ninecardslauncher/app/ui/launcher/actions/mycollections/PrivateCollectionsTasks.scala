package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.mycollections

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.Conversions
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionException, PrivateCollection}
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, GetByName}

trait PrivateCollectionsTasks
  extends Conversions {
  // TODO Move this trait to commons package

  def getPrivateCollections(implicit di: Injector, contextSupport: ContextSupport):
  ServiceDef2[Seq[PrivateCollection], AppException with CollectionException] =
    for {
      apps <- di.deviceProcess.getSavedApps(GetByName)
      collections <- di.collectionProcess.generatePrivateCollections(toSeqUnformedApp(apps))
    } yield collections

}
