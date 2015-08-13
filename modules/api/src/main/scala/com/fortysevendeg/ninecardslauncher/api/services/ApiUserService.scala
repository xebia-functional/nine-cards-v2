package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model.{AuthData, Installation, User}
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import com.fortysevendeg.rest.client.{ServiceClient, ServiceClientException}
import play.api.libs.json.{Reads, Writes}

class ApiUserService(serviceClient: ServiceClient) {

  val prefixPathUser = "/users"
  val prefixPathInstallation = "/installations"

  def login(
    user: User,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[User],
    writes: Writes[User]): ServiceDef2[ServiceClientResponse[User], HttpClientException with ServiceClientException] =
    serviceClient.post[User, User](
      path = prefixPathUser,
      headers = headers,
      body = user,
      reads = Some(reads))

  def linkAuthData(
    authData: AuthData,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[User],
    writes: Writes[AuthData]): ServiceDef2[ServiceClientResponse[User], HttpClientException with ServiceClientException] =
    serviceClient.put[AuthData, User](
      path = s"$prefixPathUser/link",
      headers = headers,
      body = authData,
      reads = Some(reads))

  def createInstallation(
    installation: Installation,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[Installation],
    writes: Writes[Installation]): ServiceDef2[ServiceClientResponse[Installation], HttpClientException with ServiceClientException] = {
    // TODO we have a error in match when the field is None. We need to fix that
    val installationCopy = Installation(
      _id = installation._id map (t => t),
      deviceType = installation.deviceType map (t => t),
      deviceToken = installation.deviceToken map (t => t),
      userId = installation.userId map (t => t))
    serviceClient.post[Installation, Installation](
      path = prefixPathInstallation,
      headers = headers,
      body = installationCopy,
      reads = Some(reads))
  }

  def updateInstallation(
    installation: Installation,
    headers: Seq[(String, String)]
    )(implicit writes: Writes[Installation]): ServiceDef2[ServiceClientResponse[Unit], HttpClientException with ServiceClientException] = {
    // TODO we have a error in match when the field is None. We need to fix that
    val installationCopy = Installation(
      _id = installation._id map (t => t),
      deviceType = installation.deviceType map (t => t),
      deviceToken = installation.deviceToken map (t => t),
      userId = installation.userId map (t => t)
    )
    serviceClient.put[Installation, Unit](
      path = s"$prefixPathInstallation/${installation._id}",
      headers = headers,
      body = installationCopy,
      reads = None,
      emptyResponse = true)
  }

}
