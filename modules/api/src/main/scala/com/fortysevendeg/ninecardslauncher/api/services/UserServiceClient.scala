package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model.{Installation, AuthData, User}
import com.fortysevendeg.rest.client.ServiceClient
import play.api.libs.json.{Writes, Reads}

import scala.concurrent.ExecutionContext

trait UserServiceClient {

  val serviceClient: ServiceClient

  val prefixPathUser = "/users"
  val prefixPathInstallation = "/installation"

  def login(
     user: User,
     headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[User], writes: Writes[User]) =
    serviceClient.post[User, User](
      path = prefixPathUser,
      headers = headers,
      body = user,
      reads = Some(reads))

  def linkAuthData(
     authData: AuthData,
     headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[User], writes: Writes[AuthData]) =
    serviceClient.put[AuthData, User](
      path = s"$prefixPathUser/link",
      headers = headers,
      body = authData,
      reads = Some(reads))

  def createInstallation(
      installation: Installation,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[Installation], writes: Writes[Installation]) =
    serviceClient.post[Installation, Installation](
      path = prefixPathInstallation,
      headers = headers,
      body = installation,
      reads = Some(reads))

  def updateInstallation(
      installation: Installation,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, writes: Writes[Installation]) =
    serviceClient.put[Installation, Unit](
      path = s"$prefixPathInstallation/${installation.id}",
      headers = headers,
      body = installation,
      reads = None,
      emptyResponse = true)

}
