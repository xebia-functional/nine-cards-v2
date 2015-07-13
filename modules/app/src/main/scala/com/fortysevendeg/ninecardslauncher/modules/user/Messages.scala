package com.fortysevendeg.ninecardslauncher.modules.user

import com.fortysevendeg.ninecardslauncher.services.api.models.GoogleDevice

case class LoginRequest(email: String, device: GoogleDevice)

case class SignInResponse(statusCode: Int)