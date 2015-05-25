package com.fortysevendeg.ninecardslauncher.modules.googleconnector

import android.app.Activity
import com.fortysevendeg.ninecardslauncher.utils.Service

trait GoogleConnectorServices {

  def requestToken(activity: Activity): Service[RequestTokenRequest, RequestTokenResponse]

  def getUser: Option[String]

  def getToken: Option[String]
}
