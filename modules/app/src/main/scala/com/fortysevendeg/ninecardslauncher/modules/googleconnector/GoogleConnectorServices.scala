package com.fortysevendeg.ninecardslauncher.modules.googleconnector

import android.app.Activity
import com.fortysevendeg.ninecardslauncher.commons.Service

trait GoogleConnectorServices {

  def requestToken(activity: Activity): Service[RequestTokenRequest, RequestTokenResponse]

}
