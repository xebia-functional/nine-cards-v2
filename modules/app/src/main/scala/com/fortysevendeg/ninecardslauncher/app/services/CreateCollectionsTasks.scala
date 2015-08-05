package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.device.{AppCategorizationException, CreateBitmapException}
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserCollection
import play.api.libs.json.Json

trait CreateCollectionsTasks
  extends Conversions {

  def createNewConfiguration(implicit context: ContextSupport, di: Injector): ServiceDef2[Seq[Collection], AppCategorizationException with CollectionException] =
    for {
      _ <- di.deviceProcess.categorizeApps
      appsCategorized <- di.deviceProcess.getCategorizedApps
      collections <- di.collectionProcess.createCollectionsFromUnformedItems(toSeqUnformedItem(appsCategorized))
    } yield collections

  def loadConfiguration(deviceId: String)(implicit context: ContextSupport, di: Injector): ServiceDef2[Seq[Collection], AppCategorizationException with CreateBitmapException with UserConfigException with CollectionException] =
    for {
      _ <- di.deviceProcess.categorizeApps
      userCollections <- di.userConfigProcess.getUserCollection(deviceId)
      appsCategorized <- di.deviceProcess.getCategorizedApps
      _ <- di.deviceProcess.createBitmapsFromPackages(getAppsNotInstalled(appsCategorized, userCollections))
      collections <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(userCollections))
    } yield collections

  private[this] def getAppsNotInstalled(appsCategorized: Seq[AppCategorized], userCollections: Seq[UserCollection]): Seq[String] = {
    import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
    val intents = userCollections flatMap (_.items map (item => Json.parse(item.intent).as[NineCardIntent]))
    intents flatMap {
      _.extractPackageName() flatMap {
        pn =>
          if (!appsCategorized.exists(_.packageName == pn)) Option(pn) else None
      }
    }
  }

}
