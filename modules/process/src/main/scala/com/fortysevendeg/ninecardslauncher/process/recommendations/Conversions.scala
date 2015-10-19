package com.fortysevendeg.ninecardslauncher.process.recommendations

import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlayApp

trait Conversions {

  def toRecommendedApp(googlePlayApp: GooglePlayApp): RecommendedApp =
    RecommendedApp(
      packageName = googlePlayApp.docid,
      title = googlePlayApp.title,
      icon = googlePlayApp.icon,
      downloads = googlePlayApp.details.appDetails.numDownloads,
      starts = googlePlayApp.aggregateRating.starRating,
      description = googlePlayApp.descriptionHtml,
      free = googlePlayApp.offer.headOption exists (_.micros == 0),
      screenshots = googlePlayApp.screenshots)

}
