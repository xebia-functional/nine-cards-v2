package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{NineCardIntent, Collection}
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserCollection
import play.api.libs.json.Json

import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Task

trait CreateCollectionsTasks
  extends Conversions {

  def createNewConfiguration(implicit context: ContextSupport, di: Injector): Task[NineCardsException \/ Seq[Collection]] =
    for {
      _ <- di.deviceProcess.categorizeApps ▹ eitherT
      appsCategorized <- di.deviceProcess.getCategorizedApps ▹ eitherT
      collections <- di.collectionProcess.createCollectionsFromUnformedItems(toSeqUnformedItem(appsCategorized)) ▹ eitherT
    } yield collections

  def loadConfiguration(deviceId: String)(implicit context: ContextSupport, di: Injector): Task[NineCardsException \/ Seq[Collection]] =
    for {
      _ <- di.deviceProcess.categorizeApps ▹ eitherT
      userCollections <- di.userConfigProcess.getUserCollection(deviceId) ▹ eitherT
      appsCategorized <- di.deviceProcess.getCategorizedApps ▹ eitherT
      _ <- di.deviceProcess.createBitmapsForNoPackagesInstalled(getAppsNotInstalled(appsCategorized, userCollections)) ▹ eitherT
      collections <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(userCollections)) ▹ eitherT
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
