package com.fortysevendeg.ninecardslauncher.api.version2

case class LoginRequest(email: String, loginId: String, tokenId: String)

case class LoginResponse(apiKey: String, sessionToken: String)