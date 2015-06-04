package com.fortysevendeg.ninecardslauncher.services.apps

import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

case class GetInstalledAppsRequest()

case class GetInstalledAppsResponse(apps: Seq[Application])