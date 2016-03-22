package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.{CardException, CollectionException}
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, GetByName}
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollection, SharedCollectionPackage}
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.{SharedCollectionsExceptions, TypeSharedCollection}

trait PublicCollectionsTasks
  extends Conversions {

  def getSharedCollections(category: NineCardCategory, typeSharedCollection: TypeSharedCollection)
    (implicit di: Injector, contextSupport: ContextSupport): ServiceDef2[Seq[SharedCollection], SharedCollectionsExceptions] =
    di.sharedCollectionsProcess.getSharedCollectionsByCategory(category, typeSharedCollection)

  def addCollection(sharedCollection: SharedCollection)(implicit di: Injector, contextSupport: ContextSupport):
  ServiceDef2[Collection, CollectionException with CardException with AppException] =
    for {
      collection <- di.collectionProcess.addCollection(toAddCollectionRequest(sharedCollection))
      appsInstalled <- di.deviceProcess.getSavedApps(GetByName)
      cards <- di.collectionProcess.addCards(collection.id, getCards(appsInstalled, sharedCollection.resolvedPackages))
    } yield collection.copy(cards = cards)

  private[this] def getCards(appsInstalled: Seq[App], packages: Seq[SharedCollectionPackage]) =
    packages map { pck =>
      appsInstalled find (_.packageName == pck.packageName) map { app =>
        toAddCollectionRequest(app)
      } getOrElse toAddCollectionRequest(pck)
    }

}
