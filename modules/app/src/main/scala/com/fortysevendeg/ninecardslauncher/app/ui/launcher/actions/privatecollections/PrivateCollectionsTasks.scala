package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, PrivateCollection}
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, GetByName}
import com.fortysevendeg.ninecardslauncher.process.moment.{MomentConversions, MomentException}

trait PrivateCollectionsTasks
  extends Conversions
  with MomentConversions {

  def getPrivateCollections(implicit di: Injector, contextSupport: ContextSupport):
  ServiceDef2[Seq[PrivateCollection], AppException with CollectionException with MomentException] =
    for {
      collections <- di.collectionProcess.getCollections
      apps <- di.deviceProcess.getSavedApps(GetByName)
      unformedApps = toSeqUnformedApp(apps)
      newCollections <- di.collectionProcess.generatePrivateCollections(unformedApps)
      newMomentCollections <- di.momentProcess.generatePrivateMoments(unformedApps map toApp, newCollections.length)
    } yield {
      val privateCollections = newCollections filterNot { newCollection =>
        newCollection.appsCategory match {
          case Some(category) => (collections flatMap (_.appsCategory)) contains category
          case _ => false
        }
      }
      val privateMoments = newMomentCollections filterNot { newMomentCollection =>
        newMomentCollection.collectionType match {
          case collectionType => (collections map (_.collectionType)) contains collectionType
          case _ => false
        }
      }
      privateCollections ++ privateMoments
    }

  def addCollection(privateCollection: PrivateCollection)(implicit di: Injector, contextSupport: ContextSupport):
  ServiceDef2[Collection, CollectionException with CardException] =
    for {
      collection <- di.collectionProcess.addCollection(toAddCollectionRequest(privateCollection))
      cards <- di.collectionProcess.addCards(collection.id, privateCollection.cards map toAddCollectionRequest)
    } yield collection.copy(cards = cards)

}
