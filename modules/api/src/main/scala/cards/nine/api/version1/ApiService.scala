package cards.nine.api.version1

import cards.nine.commons.services.TaskService.TaskService
import cards.nine.api.rest.client.ServiceClient
import cards.nine.api.rest.client.messages.ServiceClientResponse
import play.api.libs.json.{Reads, Writes}

class ApiService(serviceClient: ServiceClient) {

  val prefixPathUser = "/users"

  val prefixPathUserConfig = "/ninecards/userconfig"

  def baseUrl: String = serviceClient.baseUrl

  def login(
    user: User,
    headers: Seq[(String, String)])
    (implicit reads: Reads[User],writes: Writes[User]): TaskService[ServiceClientResponse[User]] =
    serviceClient.post[User, User](
      path = prefixPathUser,
      headers = headers,
      body = user,
      reads = Some(reads))

  def getUserConfig(
    headers: Seq[(String, String)]
  )(implicit reads: Reads[UserConfig]): TaskService[ServiceClientResponse[UserConfig]] =
    serviceClient.get[UserConfig](
      path = prefixPathUserConfig,
      headers = headers,
      reads = Some(reads))

}
