package com.fortysevendeg.ninecardslauncher.modules.appsmanager

case class GetAppsRequest()

case class GetAppsResponse(apps: Seq[AppItem])