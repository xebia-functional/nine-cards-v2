package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, ContactException, CreateBitmapException}
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserCollection
import play.api.libs.json.Json

trait CreateCollectionsTasks
  extends Conversions {

  def createNewConfiguration(implicit context: ContextSupport, di: Injector): ServiceDef2[Seq[Collection], AppException with ContactException with CollectionException] =
    for {
      saveInstalledApps <- di.deviceProcess.saveInstalledApps
      apps <- di.deviceProcess.getSavedApps
      contacts <- di.deviceProcess.getFavoriteContacts
      collections <- di.collectionProcess.createCollectionsFromUnformedItems(toSeqUnformedApp(apps), toSeqUnformedContact(contacts))
    } yield collections

  def loadConfiguration(deviceId: String)(implicit context: ContextSupport, di: Injector): ServiceDef2[Seq[Collection], AppException with UserConfigException with CreateBitmapException with CollectionException] =
    for {
      apps <- di.deviceProcess.getSavedApps
      userCollections <- di.userConfigProcess.getUserCollection(deviceId)
      bitmaps <- di.deviceProcess.createBitmapsFromPackages(getAppsNotInstalled(apps, userCollections))
      collections <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(userCollections))
    } yield collections

  private[this] def getAppsNotInstalled(apps: Seq[App], userCollections: Seq[UserCollection]): Seq[String] = {
    import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
    val intents = userCollections flatMap (_.items map (item => Json.parse(item.intent).as[NineCardIntent]))
    intents flatMap {
      _.extractPackageName() flatMap {
        pn =>
          if (!apps.exists(_.packageName == pn)) Option(pn) else None
      }
    }
  }

}
