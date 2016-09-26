package com.fortysevendeg.ninecardslauncher.process.recommendations

import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher.services.api.RecommendationApp

trait Conversions {

  def toRecommendedApp(app: RecommendationApp): RecommendedApp =
    RecommendedApp(
      packageName = app.packageName,
      title = app.name,
      icon = Some(app.icon),
      downloads = app.downloads,
      stars = app.stars,
      free = app.free,
      screenshots = app.screenshots)

}
