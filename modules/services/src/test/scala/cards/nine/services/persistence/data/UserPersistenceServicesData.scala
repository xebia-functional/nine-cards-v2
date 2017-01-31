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

package cards.nine.services.persistence.data

import cards.nine.commons.test.data.UserValues._
import cards.nine.repository.model.{User, UserData}

trait UserPersistenceServicesData {

  def repoUserData(num: Int = 0) =
    UserData(
      email = Option(email),
      sessionToken = Option(sessionToken),
      apiKey = Option(apiKey),
      deviceToken = Option(deviceToken),
      marketToken = Option(marketToken),
      deviceName = Option(userDeviceName),
      deviceCloudId = Option(deviceCloudId),
      name = Option(userName),
      avatar = Option(avatar),
      cover = Option(cover))

  val repoUserData: UserData         = repoUserData(0)
  val seqRepoUserData: Seq[UserData] = Seq(repoUserData(0), repoUserData(1), repoUserData(2))

  def repoUser(num: Int = 0) = User(id = userId + num, data = repoUserData(num))

  val repoUser: User         = repoUser(0)
  val seqRepoUser: Seq[User] = Seq(repoUser(0), repoUser(1), repoUser(2))

}
