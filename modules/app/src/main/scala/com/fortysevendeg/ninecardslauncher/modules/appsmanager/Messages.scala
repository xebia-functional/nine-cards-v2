package com.fortysevendeg.ninecardslauncher.modules.appsmanager

import com.fortysevendeg.ninecardslauncher.models.AppItem
import com.fortysevendeg.ninecardslauncher.services.api.models.NineCardIntent

case class GetAppsRequest()

case class GetAppsResponse(apps: Seq[AppItem])

case class GetCategorizedAppsRequest()

case class GetCategorizedAppsResponse(apps: Seq[AppItem])

case class GetAppsByCategoryRequest(category: String)

case class GetAppsByCategoryResponse(apps: Seq[AppItem])

case class CategorizeAppsRequest()

case class CategorizeAppsResponse()

case class IntentsRequest(intents: Seq[NineCardIntent])

case class PackagesResponse(packages: Seq[String])