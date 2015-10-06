package com.fortysevendeg.ninecardslauncher.process.recommendations.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.ResultTExtensions
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher.process.recommendations.{ImplicitsRecommendationsException, RecommendedAppsException, Conversions, RecommendationsProcess}
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices

class RecommendationsProcessImpl(apiServices: ApiServices)
  extends RecommendationsProcess
  with Conversions
  with ImplicitsRecommendationsException {

  val defaultRecommendedAppsLimit = 10

  override def getRecommendedAppsByCategory(category: String): ServiceDef2[Seq[RecommendedApp], RecommendedAppsException] =
    (apiServices.getRecommendedApps(Seq(category), defaultRecommendedAppsLimit) map { response =>
      response.seq map toRecommendedApp
    }).resolve[RecommendedAppsException]

}
