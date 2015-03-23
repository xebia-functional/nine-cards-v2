package com.fortysevendeg.ninecardslauncher.modules.repository

import com.fortysevendeg.ninecardslauncher.modules.appsmanager.AppItem

// TODO Remove apps field. We are using this field until the repository component will be finished
case class GetCollectionsRequest(
  apps: Seq[AppItem])

case class GetCollectionsResponse(collections: Seq[Collection])
