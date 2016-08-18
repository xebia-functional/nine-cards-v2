package com.fortysevendeg.ninecardslauncher.api.version1.services

import com.fortysevendeg.ninecardslauncher.api.version1.model.UserConfig
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService._
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import play.api.libs.json.Reads

class ApiUserConfigService(serviceClient: ServiceClient) {

  val prefixPathUserConfig = "/ninecards/userconfig"

  def getUserConfig(
    headers: Seq[(String, String)]
    )(implicit reads: Reads[UserConfig]): CatsService[ServiceClientResponse[UserConfig]] =
    serviceClient.get[UserConfig](
      path = prefixPathUserConfig,
      headers = headers,
      reads = Some(reads))

}
