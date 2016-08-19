package com.fortysevendeg.ninecardslauncher.api.version1

import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.rest.client.{ServiceClient, ServiceClientException}
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import play.api.libs.json.{Reads, Writes}

class ApiService(serviceClient: ServiceClient) {

  val prefixPathUser = "/users"

  val prefixPathUserConfig = "/ninecards/userconfig"

  def login(
    user: User,
    headers: Seq[(String, String)])
    (implicit reads: Reads[User],writes: Writes[User]): ServiceDef2[ServiceClientResponse[User], HttpClientException with ServiceClientException] =
    serviceClient.post[User, User](
      path = prefixPathUser,
      headers = headers,
      body = user,
      reads = Some(reads))

  def getUserConfig(
    headers: Seq[(String, String)]
  )(implicit reads: Reads[UserConfig]): ServiceDef2[ServiceClientResponse[UserConfig], HttpClientException with ServiceClientException] =
    serviceClient.get[UserConfig](
      path = prefixPathUserConfig,
      headers = headers,
      reads = Some(reads))

}
