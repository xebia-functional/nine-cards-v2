package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppItem
import com.fortysevendeg.ninecardslauncher.services.api.models.{User, GooglePlaySimplePackages}
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory
import com.fortysevendeg.rest.client.ServiceClient

import scalaz._
import Scalaz._
import EitherT._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.concurrent.Task

class DeviceProcessImpl(
    appsService: AppsServices,
    serviceClient: ServiceClient,
    apiServices: ApiServices,
    persistenceServices: PersistenceServices)
    extends DeviceProcess
    with DeviceConversions {

  override def getApps(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppItem]] =
    appsService.getInstalledApps ▹ eitherT map toAppItemSeq

  override def getAppsByCategory(category: String)(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppItem]] =
    getCategorizedApps ▹ eitherT map (_.filter(_.category.contains(category)))


  override def getCategorizedApps(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppItem]] =
    for {
      cacheCategories <- persistenceServices.fetchCacheCategories ▹ eitherT
      apps <- getApps ▹ eitherT
    } yield {
      apps map {
        app =>
          app.copy(category = cacheCategories.find(_.packageName == app.packageName).map(_.category))
      }
    }

  override def categorizeApps()(implicit context: ContextSupport): Task[NineCardsException \/ Unit] = {
    for {
      apps <- getCategorizedApps ▹ eitherT
      packagesWithoutCategory = apps.filter(_.category.isEmpty) map (_.packageName)
      androidIdAndToken <- getTokenAndAndroidId ▹ eitherT
      response <- apiServices.googlePlaySimplePackages(GooglePlaySimplePackagesRequest(androidIdAndToken.androidId, androidIdAndToken.token, packagesWithoutCategory)) ▹ eitherT
      _ <- insertRepositories(response.apps) ▹ eitherT
    } yield ()
  }

  private[this] def insertRepositories(packages: GooglePlaySimplePackages): Task[NineCardsException \/ List[CacheCategory]] = {
    val tasks = packages.items map {
      app =>
        persistenceServices.addCacheCategory(AddCacheCategoryRequest(
          packageName = app.packageName,
          category = app.appCategory,
          starRating = app.starRating,
          numDownloads = app.numDownloads,
          ratingsCount = app.ratingCount,
          commentCount = app.commentCount
        ))
    }
    Task.gatherUnordered(tasks) map (_.collect { case \/-(category) => category }.right[NineCardsException])
  }

  case class AndroidIdAndToken(androidId: String, token: String)

  private def getTokenAndAndroidId()(implicit context: ContextSupport): Task[NineCardsException \/ AndroidIdAndToken] = {
    val tokenTask = getSessionToken map {
      case -\/(ex) => -\/(NineCardsException(msg = "Android Id not found", cause = ex.some))
      case \/-(r) => \/-(r)
    }
    for {
      token <- tokenTask ▹ eitherT
      androidId <- persistenceServices.getAndroidId ▹ eitherT
    } yield AndroidIdAndToken(androidId = androidId, token = token)
  }

  private def getSessionToken(implicit context: ContextSupport): Task[NineCardsException \/ String] =
    persistenceServices.getUser map {
      case \/-(User(_, Some(sessionToken), _, _)) => \/-(sessionToken) //TODO refactor to named params once available in Scala
      case -\/(ex) => -\/(ex)
      case _ => -\/(NineCardsException("Session token doesn't exists"))
    }

}

