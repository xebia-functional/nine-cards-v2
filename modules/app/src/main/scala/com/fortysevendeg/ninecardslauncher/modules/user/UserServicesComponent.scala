package com.fortysevendeg.ninecardslauncher.modules.user

import com.fortysevendeg.ninecardslauncher.services.api.models.{Installation, User}

import scala.concurrent.Future

trait UserServices {
  def register(): Unit
  def unregister(): Unit
  def getUser: Option[User]
  def getInstallation: Option[Installation]
  def signIn: LoginRequest => Future[SignInResponse]
  def getAndroidId: Option[String]
}

trait UserServicesComponent {
  val userServices: UserServices
}

