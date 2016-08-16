package com.fortysevendeg.ninecardslauncher.api.version2

import com.fortysevendeg.rest.client.ServiceClient
import play.api.libs.json.{Reads, Writes}

class ApiService(serviceClient: ServiceClient) {

  def login(login: LoginRequest)(implicit reads: Reads[LoginResponse], writes: Writes[LoginRequest]) =
    serviceClient.post[LoginRequest, LoginResponse](
      path = "/login",
      body = login,
      reads = Some(reads))

}
