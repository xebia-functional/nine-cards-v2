package com.fortysevendeg.ninecardslauncher.modules.image

case class StoreImageAppRequest(packageName: String, url: String)

case class StoreImageAppResponse(packageName: Option[String])
