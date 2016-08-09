package com.fortysevendeg.ninecardslauncher.api.version1.services

import com.fortysevendeg.ninecardslauncher.api.version1.model.UserConfig
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import com.fortysevendeg.rest.client.{ServiceClient, ServiceClientException}
import play.api.libs.json.Reads

class ApiUserConfigService(serviceClient: ServiceClient) {

  val prefixPathUserConfig = "/ninecards/userconfig"

  def getUserConfig(
    headers: Seq[(String, String)]
    )(implicit reads: Reads[UserConfig]): ServiceDef2[ServiceClientResponse[UserConfig], HttpClientException with ServiceClientException] =
    serviceClient.get[UserConfig](
      path = prefixPathUserConfig,
      headers = headers,
      reads = Some(reads))

}
