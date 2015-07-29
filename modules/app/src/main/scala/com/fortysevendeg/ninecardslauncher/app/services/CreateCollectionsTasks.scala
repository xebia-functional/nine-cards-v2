package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, FormedCollection, NineCardIntent, UnformedItem}
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserCollection
import com.fortysevendeg.ninecardslauncher.services.apps.AppsInstalledException
import com.fortysevendeg.ninecardslauncher.services.image.BitmapTransformationException
import com.fortysevendeg.ninecardslauncher.services.persistence.RepositoryException
import play.api.libs.json.Json
import rapture.core.Result

import scalaz.Scalaz._
import scalaz._

trait CreateCollectionsTasks
  extends Conversions {

  def createNewConfiguration(implicit context: ContextSupport, di: Injector): ServiceDef2[Seq[Collection], AppsInstalledException with BitmapTransformationException with NineCardsException with RepositoryException] =
    for {
      _ <- di.deviceProcess.categorizeApps
      appsCategorized <- di.deviceProcess.getCategorizedApps
      collections <- createCollectionsFromUnformedItems(toSeqUnformedItem(appsCategorized))
    } yield collections

  def loadConfiguration(deviceId: String)(implicit context: ContextSupport, di: Injector): ServiceDef2[Seq[Collection], RepositoryException with AppsInstalledException with BitmapTransformationException with NineCardsException] =
    for {
      _ <- di.deviceProcess.categorizeApps
      userCollections <- getUserCollection(deviceId)
      appsCategorized <- di.deviceProcess.getCategorizedApps
      _ <- di.deviceProcess.createBitmapsFromPackages(getAppsNotInstalled(appsCategorized, userCollections))
      collections <- createCollectionsFromFormedCollections(toSeqFormedCollection(userCollections))
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

  private[this] def createCollectionsFromUnformedItems(items: Seq[UnformedItem])(implicit context: ContextSupport, di: Injector):
  ServiceDef2[Seq[Collection], NineCardsException] = Service {
    di.collectionProcess.createCollectionsFromUnformedItems(items) map {
      case -\/(ex) => Result.errata(NineCardsException(msg = "createCollectionsFromUnformedItems error", cause = ex.some))
      case \/-(r) => Result.answer(r)
    }
  }

  private[this] def getUserCollection(deviceId: String)(implicit context: ContextSupport, di: Injector):
  ServiceDef2[Seq[UserCollection], NineCardsException] = Service {
    di.userConfigProcess.getUserCollection(deviceId) map {
      case -\/(ex) => Result.errata(NineCardsException(msg = "getUserCollection error", cause = ex.some))
      case \/-(r) => Result.answer(r)
    }
  }

//  private[this] def createBitmapsFromPackages(pacs: Seq[String])(implicit context: ContextSupport, di: Injector):
//  ServiceDef2[Unit, NineCardsException] = Service {
//    di.deviceProcess.createBitmapsFromPackages(pacs) map {
//      case -\/(ex) => Result.errata(NineCardsException(msg = "createBitmapsFromPackages error", cause = ex.some))
//      case \/-(r) => Result.answer(r)
//    }
//  }

  private[this] def createCollectionsFromFormedCollections(fc: Seq[FormedCollection])(implicit context: ContextSupport, di: Injector):
  ServiceDef2[Seq[Collection], NineCardsException] = Service {
    di.collectionProcess.createCollectionsFromFormedCollections(fc) map {
      case -\/(ex) => Result.errata(NineCardsException(msg = "createCollectionsFromFormedCollections error", cause = ex.some))
      case \/-(r) => Result.answer(r)
    }
  }

}
