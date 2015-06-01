package com.fortysevendeg.ninecardslauncher.modules.user

import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.services.api.LoginRequest
import com.fortysevendeg.ninecardslauncher.services.api.models.{Installation, User}

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

