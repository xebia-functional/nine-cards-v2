package cards.nine.app.ui.collections.tasks

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.contexts.ActivityContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.GetByName
import cards.nine.models.{ApplicationData, Collection, SharedCollection, SharedCollectionPackage}

trait CollectionJobs {

  self: Jobs with Conversions =>

  def addSharedCollection(sharedCollection: SharedCollection)(implicit contextSupport: ActivityContextSupport): TaskService[Collection] = {

    def getCards(appsInstalled: Seq[ApplicationData], packages: Seq[SharedCollectionPackage]) =
      packages map { pck =>
        appsInstalled find (_.packageName == pck.packageName) map { app =>
          toCardData(app)
        } getOrElse toCardData(pck)
      }

    for {
      appsInstalled <- di.deviceProcess.getSavedApps(GetByName)
      collection <- di.collectionProcess.addCollection(toCollectionDataFromSharedCollection(sharedCollection, getCards(appsInstalled, sharedCollection.resolvedPackages)))
    } yield collection
  }

}
