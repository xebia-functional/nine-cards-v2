package com.fortysevendeg.ninecardslauncher.modules.user

import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.api.{Installation, User, LoginResponse, LoginRequest}

trait UserServices {
  def register(): Unit
  def unregister(): Unit
  def getUser: Option[User]
  def getInstallation: Option[Installation]
  def signIn: Service[LoginRequest, SignInResponse]
  def getAndroidId: Option[String]
}

trait UserServicesComponent {
  val userServices: UserServices
}

