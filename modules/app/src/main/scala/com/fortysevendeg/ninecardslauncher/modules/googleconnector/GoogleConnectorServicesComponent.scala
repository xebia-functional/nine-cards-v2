package com.fortysevendeg.ninecardslauncher.modules.googleconnector

import com.fortysevendeg.ninecardslauncher.commons._
import macroid.ActivityContext

trait GoogleConnectorServices {
  def requestToken(implicit activityContext: ActivityContext): Service[RequestTokenRequest, RequestTokenResponse]
  def getUser: Option[String]
  def getToken: Option[String]
}

trait GoogleConnectorServicesComponent {
  val googleConnectorServices: GoogleConnectorServices
}