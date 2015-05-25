package com.fortysevendeg.ninecardslauncher.modules.user

import com.fortysevendeg.ninecardslauncher.models.GoogleDevice

import scala.concurrent.Future

trait UserService {

  def register(): Unit

  def unregister(): Unit

  def signIn(email: String, device: GoogleDevice): Future[Int]

}
