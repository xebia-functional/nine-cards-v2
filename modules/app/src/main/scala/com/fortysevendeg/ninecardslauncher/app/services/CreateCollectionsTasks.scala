package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.app.commons.Broadkast
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.device.{ContactException, AppCategorizationException, CreateBitmapException}
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserCollection
import play.api.libs.json.Json
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ActionFilters._

trait CreateCollectionsTasks
  extends Conversions {

  self : Broadkast =>

  def createNewConfiguration(implicit context: ContextSupport, di: Injector): ServiceDef2[Seq[Collection], AppCategorizationException with ContactException with CollectionException] =
    for {
      _ <- di.deviceProcess.categorizeApps
      appsCategorized <- di.deviceProcess.getCategorizedApps
      contacts <- di.deviceProcess.getFavoriteContacts
      collections <- di.collectionProcess.createCollectionsFromUnformedItems(toSeqUnformedApp(appsCategorized), toSeqUnformedContact(contacts))
    } yield collections

  def loadConfiguration(deviceId: String)(implicit context: ContextSupport, di: Injector): ServiceDef2[Seq[Collection], AppCategorizationException with CreateBitmapException with UserConfigException with CollectionException] =
    for {
      _ <- di.deviceProcess.categorizeApps
      _ = self ! BroadAction(testFilter, Option("Apps categorized"))
      userCollections <- di.userConfigProcess.getUserCollection(deviceId)
      appsCategorized <- di.deviceProcess.getCategorizedApps
      _ = self ! BroadAction(testFilter, Option("User collections"))
      _ <- di.deviceProcess.createBitmapsFromPackages(getAppsNotInstalled(appsCategorized, userCollections))
      _ = self ! BroadAction(testFilter, Option("Bitmap created"))
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
