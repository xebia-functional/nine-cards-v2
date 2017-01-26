/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.api.version1

import cards.nine.commons.services.TaskService.TaskService
import cards.nine.api.rest.client.ServiceClient
import cards.nine.api.rest.client.messages.ServiceClientResponse
import play.api.libs.json.{Reads, Writes}

class ApiService(serviceClient: ServiceClient) {

  val prefixPathUser = "/users"

  val prefixPathUserConfig = "/ninecards/userconfig"

  def baseUrl: String = serviceClient.baseUrl

  def login(user: User, headers: Seq[(String, String)])(
      implicit reads: Reads[User],
      writes: Writes[User]): TaskService[ServiceClientResponse[User]] =
    serviceClient
      .post[User, User](path = prefixPathUser, headers = headers, body = user, reads = Some(reads))

  def getUserConfig(
      headers: Seq[(String, String)]
  )(implicit reads: Reads[UserConfig]): TaskService[ServiceClientResponse[UserConfig]] =
    serviceClient
      .get[UserConfig](path = prefixPathUserConfig, headers = headers, reads = Some(reads))

}
