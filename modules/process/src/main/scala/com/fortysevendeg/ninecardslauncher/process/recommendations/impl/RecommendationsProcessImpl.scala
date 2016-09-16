package com.fortysevendeg.ninecardslauncher.process.recommendations.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.recommendations._
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceConfigurationException, ApiServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices


class RecommendationsProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends RecommendationsProcess
  with Conversions {

  val apiUtils = new ApiUtils(persistenceServices)

  val defaultRecommendedAppsLimit = 20

  override def getRecommendedAppsByCategory(category: NineCardCategory, excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getRecommendedApps(category.name, excludePackages, defaultRecommendedAppsLimit)(userConfig)
    } yield response.seq map toRecommendedApp).resolveLeft(mapLeft)

  override def getRecommendedAppsByPackages(packages: Seq[String], excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getRecommendedAppsByPackages(packages, excludePackages, defaultRecommendedAppsLimit)(userConfig)
    } yield response.seq map toRecommendedApp).resolveLeft(mapLeft)

  private[this] def mapLeft[T]: (NineCardException) => Either[NineCardException, T] = {
    case e: ApiServiceConfigurationException => Left(RecommendedAppsConfigurationException(e.message, Some(e)))
    case e => Left(RecommendedAppsException(e.message, Some(e)))
  }

}
