package com.fortysevendeg.ninecardslauncher.modules.appsmanager

import com.fortysevendeg.ninecardslauncher.models.AppItem

case class GetAppsRequest()

case class GetAppsResponse(apps: Seq[AppItem])

case class GetCategorizedAppsRequest()

case class GetCategorizedAppsResponse(apps: Seq[AppItem])

case class GetAppsByCategoryRequest(category: String)

case class GetAppsByCategoryResponse(apps: Seq[AppItem])

case class CategorizeAppsRequest()

case class CategorizeAppsResponse(success: Boolean)