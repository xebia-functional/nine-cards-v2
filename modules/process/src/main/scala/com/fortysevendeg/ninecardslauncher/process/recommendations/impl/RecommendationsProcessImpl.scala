package com.fortysevendeg.ninecardslauncher.process.recommendations.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.recommendations._
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import cards.nine.services.api.{ApiServiceConfigurationException, ApiServices}
import cards.nine.services.persistence.PersistenceServices


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
