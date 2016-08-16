package com.fortysevendeg.ninecardslauncher.api.version2

import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.rest.client.{ServiceClient, ServiceClientException}
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import play.api.libs.json.{Reads, Writes}

class ApiService(serviceClient: ServiceClient) {

  def login(login: LoginRequest)(
    implicit reads: Reads[LoginResponse], writes: Writes[LoginRequest]): ServiceDef2[ServiceClientResponse[LoginResponse], HttpClientException with ServiceClientException] =
    serviceClient.post[LoginRequest, LoginResponse](
      path = "/login",
      headers = Seq.empty,
      body = login,
      reads = Some(reads))

}
