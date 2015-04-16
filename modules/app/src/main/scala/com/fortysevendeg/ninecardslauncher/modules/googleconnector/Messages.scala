package com.fortysevendeg.ninecardslauncher.modules.googleconnector

case class RequestTokenRequest(username: String)

case class RequestTokenResponse(token: String = "")