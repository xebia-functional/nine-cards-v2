package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model.{Installation, AuthData, User}
import com.fortysevendeg.rest.client.ServiceClient
import play.api.libs.json.{Json, Writes, Reads}

import scala.concurrent.ExecutionContext

trait UserServiceClient {

  val serviceClient: ServiceClient

  val prefixPathUser = "/users"
  val prefixPathInstallation = "/installations"

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
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[Installation], writes: Writes[Installation]) = {
    // TODO we have a error in match when the field is None. We have fixing that
    val installationCopy = Installation(
      _id = installation._id map (t => t),
      deviceType = installation.deviceType map (t => t),
      deviceToken = installation.deviceToken  map (t => t),
      userId = installation.userId  map (t => t)
    )
    serviceClient.post[Installation, Installation](
      path = prefixPathInstallation,
      headers = headers,
      body = installationCopy,
      reads = Some(reads))
  }

  def updateInstallation(
      installation: Installation,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, writes: Writes[Installation]) = {
    // TODO we have a error in match when the field is None. We have fixing that
    val installationCopy = Installation(
      _id = installation._id map (t => t),
      deviceType = installation.deviceType map (t => t),
      deviceToken = installation.deviceToken  map (t => t),
      userId = installation.userId  map (t => t)
    )
    serviceClient.put[Installation, Unit](
      path = s"$prefixPathInstallation/${installation._id}",
      headers = headers,
      body = installationCopy,
      reads = None,
      emptyResponse = true)
  }

}
