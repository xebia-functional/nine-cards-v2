package com.fortysevendeg.ninecardslauncher.modules.user

import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.api.{LoginResponse, LoginRequest}

trait UserServices {
  def register(): Unit
  def unregister(): Unit
  def login(login: LoginRequest): Unit
}

trait UserServicesComponent {
  val userServices: UserServices
}

