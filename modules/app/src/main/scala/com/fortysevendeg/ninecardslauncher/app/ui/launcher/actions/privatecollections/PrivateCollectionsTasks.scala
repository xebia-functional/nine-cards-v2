package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, GetByName}

trait PrivateCollectionsTasks
  extends Conversions {

  def getPrivateCollections(implicit di: Injector, contextSupport: ContextSupport):
  ServiceDef2[Seq[PrivateCollection], AppException with CollectionException] =
    for {
      collections <- di.collectionProcess.getCollections
      apps <- di.deviceProcess.getSavedApps(GetByName)
      newCollections <- di.collectionProcess.generatePrivateCollections(toSeqUnformedApp(apps))
    } yield newCollections filterNot { newCollection =>
      newCollection.appsCategory match {
        case Some(category) => (collections flatMap (_.appsCategory)) contains category
        case _ => false
      }
    }

  def addCollection(privateCollection: PrivateCollection)(implicit di: Injector, contextSupport: ContextSupport):
  ServiceDef2[Collection, CollectionException with CardException] =
    for {
      collection <- di.collectionProcess.addCollection(toAddCollectionRequest(privateCollection))
      cards <- di.collectionProcess.addCards(collection.id, privateCollection.cards map toAddCollectionRequest)
    } yield collection.copy(cards = cards)

}
