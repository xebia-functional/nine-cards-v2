package cards.nine.app.ui.collections.tasks

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ActivityContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.models.Application.ApplicationDataOps
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{GetByName, PublishedByOther}
import cards.nine.models.{ApplicationData, Collection, SharedCollection, SharedCollectionPackage}

trait CollectionJobs {

  self: Jobs with Conversions =>

  def addSharedCollection(sharedCollection: SharedCollection)(implicit contextSupport: ActivityContextSupport): TaskService[Collection] = {

    def getCards(appsInstalled: Seq[ApplicationData], packages: Seq[SharedCollectionPackage]) =
      packages map { pck =>
        appsInstalled find (_.packageName == pck.packageName) map (_.toCardData) getOrElse toCardData(pck)
      }

    for {
      appsInstalled <- di.deviceProcess.getSavedApps(GetByName)
      collection <- di.collectionProcess.addCollection(toCollectionDataFromSharedCollection(sharedCollection, getCards(appsInstalled, sharedCollection.resolvedPackages)))
      _ <- sharedCollection.publicCollectionStatus match {
        case PublishedByOther =>
          di.sharedCollectionsProcess.subscribe(sharedCollection.sharedCollectionId).resolveLeftTo(Right((): Unit))
        case _ => TaskService.empty
      }
    } yield collection
  }

}
