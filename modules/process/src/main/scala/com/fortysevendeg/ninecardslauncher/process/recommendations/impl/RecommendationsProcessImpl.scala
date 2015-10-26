package com.fortysevendeg.ninecardslauncher.process.recommendations.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.ResultTExtensions
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.recommendations.{ImplicitsRecommendationsException, RecommendedAppsException, Conversions, RecommendationsProcess}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices

class RecommendationsProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends RecommendationsProcess
  with Conversions
  with ImplicitsRecommendationsException {

  val apiUtils = new ApiUtils(persistenceServices)

  val defaultRecommendedAppsLimit = 20

  override def getRecommendedAppsByCategory(category: String, excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getRecommendedApps(Seq(category), Seq.empty, excludePackages, defaultRecommendedAppsLimit)(userConfig)
    } yield response.seq map toRecommendedApp).resolve[RecommendedAppsException]

  override def getRecommendedAppsByPackages(packages: Seq[String], excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getRecommendedApps(Seq.empty, packages, excludePackages, defaultRecommendedAppsLimit)(userConfig)
    } yield response.seq map toRecommendedApp).resolve[RecommendedAppsException]

}
