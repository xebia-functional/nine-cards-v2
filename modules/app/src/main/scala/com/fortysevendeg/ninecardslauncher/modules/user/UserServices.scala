package com.fortysevendeg.ninecardslauncher.modules.user

import com.fortysevendeg.ninecardslauncher.models.{User, Installation}
import com.fortysevendeg.ninecardslauncher.modules.api.LoginRequest
import com.fortysevendeg.ninecardslauncher.utils.Service

trait UserServices {

  def register(): Unit

  def unregister(): Unit

  def getUser: Option[User]

  def getInstallation: Option[Installation]

  def signIn: Service[LoginRequest, SignInResponse]

  def getAndroidId: Option[String]
}
