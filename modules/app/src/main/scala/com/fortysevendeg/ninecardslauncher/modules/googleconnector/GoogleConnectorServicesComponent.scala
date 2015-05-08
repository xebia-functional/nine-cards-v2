package com.fortysevendeg.ninecardslauncher.modules.googleconnector

import com.fortysevendeg.ninecardslauncher.commons._
import macroid.ActivityContextWrapper

trait GoogleConnectorServices {
  def requestToken(implicit activityContext: ActivityContextWrapper): Service[RequestTokenRequest, RequestTokenResponse]
  def getUser: Option[String]
  def getToken: Option[String]
}

trait GoogleConnectorServicesComponent {
  val googleConnectorServices: GoogleConnectorServices
}