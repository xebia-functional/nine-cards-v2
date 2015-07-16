package com.fortysevendeg.ninecardslauncher.process.device

import com.fortysevendeg.ninecardslauncher.process.device.models.AppItem

case class GetCategorizedAppsRequest()

case class GetCategorizedAppsResponse(apps: Seq[AppItem])

case class GetAppsByCategoryRequest(category: String)

case class GetAppsByCategoryResponse(apps: Seq[AppItem])

case class CategorizeAppsRequest()

case class CategorizeAppsResponse()