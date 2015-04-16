package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model.{Installation, AuthData, User}
import com.fortysevendeg.ninecardslauncher.api.reads.UserImplicits
import com.fortysevendeg.rest.client.ServiceClient
import play.api.libs.json.Reads

import scala.concurrent.ExecutionContext

trait UserClient
  extends ServiceClient
  with UserImplicits {

  val prefixPathUser = "/users"
  val prefixPathInstallation = "/installation"

  def login(
     user: User,
     headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[User]) =
    post[User, User](
      path = prefixPathUser,
      headers = headers,
      body = user,
      Some(reads))

  def linkAuthData(
     authData: AuthData,
     headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[User]) =
    put[AuthData, User](
      path = s"$prefixPathUser/link",
      headers = headers,
      body = authData,
      Some(reads))

  def createInstallation(
      installation: Installation,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[Installation]) =
    post[Installation, Installation](
      path = prefixPathInstallation,
      headers = headers,
      body = installation,
      Some(reads))

  def updateInstallation(
      installation: Installation,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[Unit]) =
    put[Installation, Unit](
      path = s"$prefixPathInstallation/${installation._id}",
      headers = headers,
      body = installation,
      Some(reads))

}
