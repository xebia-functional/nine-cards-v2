package com.fortysevendeg.ninecardslauncher.app.ui.collections.tasks

import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.commons.contexts.ActivityContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.GetByName
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollection, SharedCollectionPackage}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

trait CollectionJobs {

  self: Jobs with Conversions =>

  def addSharedCollection(sharedCollection: SharedCollection)(implicit contextSupport: ActivityContextSupport): TaskService[Collection] = {

    def getCards(appsInstalled: Seq[App], packages: Seq[SharedCollectionPackage]) =
      packages map { pck =>
        appsInstalled find (_.packageName == pck.packageName) map { app =>
          toAddCardRequest(app)
        } getOrElse toAddCardRequest(pck)
      }

    for {
      appsInstalled <- di.deviceProcess.getSavedApps(GetByName)
      collection <- di.collectionProcess.addCollection(toAddCollectionRequestFromSharedCollection(sharedCollection, getCards(appsInstalled, sharedCollection.resolvedPackages)))
    } yield collection
  }


}
