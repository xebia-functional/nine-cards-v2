package com.fortysevendeg.ninecardslauncher.app.ui.collections.tasks

import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import cards.nine.commons.contexts.ActivityContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.process.commons.models.Collection
import cards.nine.process.device.GetByName
import cards.nine.process.device.models.App
import cards.nine.process.sharedcollections.models.{SharedCollection, SharedCollectionPackage}

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
