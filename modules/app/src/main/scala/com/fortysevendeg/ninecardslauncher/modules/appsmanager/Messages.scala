package com.fortysevendeg.ninecardslauncher.modules.appsmanager

case class GetAppsRequest()

case class GetAppsResponse(apps: Seq[AppItem])

case class GetCategorizedAppsRequest()

case class GetCategorizedAppsResponse(apps: Seq[AppItem])

case class GetAppsByCategoryRequest(category: String)

case class GetAppsByCategoryResponse(apps: Seq[AppItem])