package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.device.{GetByName, AppException, ContactException, CreateBitmapException}
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserCollection
import play.api.libs.json.Json

trait CreateCollectionsTasks
  extends Conversions {

  self: CreateCollectionService =>

  def createNewConfiguration: ServiceDef2[Seq[Collection], AppException with ContactException with CollectionException] =
    for {
      _ <- di.deviceProcess.saveInstalledApps
      _ = setProcess(GettingAppsProcess)
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ = setProcess(LoadingConfigProcess)
      contacts <- di.deviceProcess.getFavoriteContacts
      _ = setProcess(CreatingCollectionsProcess)
      collections <- di.collectionProcess.createCollectionsFromUnformedItems(toSeqUnformedApp(apps), toSeqUnformedContact(contacts))
    } yield collections

   def loadConfiguration(deviceId: String): ServiceDef2[Seq[Collection], AppException with CreateBitmapException with UserConfigException with CollectionException] =
    for {
      _ <- di.deviceProcess.saveInstalledApps
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ = setProcess(GettingAppsProcess)
      userCollections <- di.userConfigProcess.getUserCollection(deviceId)
      _ = setProcess(LoadingConfigProcess)
      bitmaps <- di.deviceProcess.createBitmapsFromPackages(getAppsNotInstalled(apps, userCollections))
      _ = setProcess(CreatingCollectionsProcess)
      collections <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(userCollections))
    } yield collections

  private[this] def getAppsNotInstalled(apps: Seq[App], userCollections: Seq[UserCollection]): Seq[String] = {
    import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
    val intents = userCollections flatMap (_.items map (item => Json.parse(item.intent).as[NineCardIntent]))
    intents flatMap {
      _.extractPackageName() flatMap { pn =>
        if (!apps.exists(_.packageName == pn)) Option(pn) else None
      }
    }
  }

}
