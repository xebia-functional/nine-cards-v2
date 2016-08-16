package com.fortysevendeg.ninecardslauncher.api.version2

case class LoginRequest(email: String, androidId: String, tokenId: String)

case class LoginResponse(apiKey: String, sessionToken: String)

case class InstallationRequest(deviceToken: String)

case class InstallationResponse(androidId: String, deviceToken: String)