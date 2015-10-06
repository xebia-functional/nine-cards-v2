package com.fortysevendeg.ninecardslauncher.process.recommendations

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp

trait RecommendationsProcess {

  /**
   * Get recommended apps based on a category
   * @param category a valid category identification
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp]
   * @throws RecommendedAppsException if there was an error fetching the recommended apps
   */
  def getRecommendedAppsByCategory(category: String): ServiceDef2[Seq[RecommendedApp], RecommendedAppsException]

}
