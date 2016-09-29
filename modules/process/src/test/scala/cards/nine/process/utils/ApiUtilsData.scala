package cards.nine.process.utils

import cards.nine.services.persistence.models.User

trait ApiUtilsData {

  val androidId = "012354654894654654"

  val apiKey = "api-key"

  val sessionToken = "Session token"

  val marketToken = "Market token"

  val userId = 1

  val user = User(
    id = userId,
    email = None,
    apiKey = Some(apiKey),
    sessionToken = Some(sessionToken),
    deviceToken = None,
    marketToken = Some(marketToken),
    name = None,
    avatar = None,
    cover = None,
    deviceName = None,
    deviceCloudId = None)

}
