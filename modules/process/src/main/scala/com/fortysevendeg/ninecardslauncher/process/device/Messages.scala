package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.services.api.models.NineCardIntent

case class GetCategorizedAppsRequest()

case class GetCategorizedAppsResponse(apps: Seq[AppCategorized])

case class GetAppsByCategoryRequest(category: String)

case class GetAppsByCategoryResponse(apps: Seq[AppCategorized])

case class CategorizeAppsRequest()

case class CategorizeAppsResponse()