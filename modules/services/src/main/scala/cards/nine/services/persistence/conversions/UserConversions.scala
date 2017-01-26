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

package cards.nine.services.persistence.conversions

import cards.nine.models.{User, UserData, UserProfile}
import cards.nine.repository.model.{User => RepositoryUser, UserData => RepositoryUserData}
import cards.nine.services.persistence._

trait UserConversions {

  def toUser(user: RepositoryUser): User =
    User(
      id = user.id,
      email = user.data.email,
      apiKey = user.data.apiKey,
      sessionToken = user.data.sessionToken,
      deviceToken = user.data.deviceToken,
      marketToken = user.data.marketToken,
      deviceName = user.data.deviceName,
      deviceCloudId = user.data.deviceCloudId,
      userProfile =
        UserProfile(name = user.data.name, avatar = user.data.avatar, cover = user.data.cover))

  def toRepositoryUser(user: User): RepositoryUser =
    RepositoryUser(
      id = user.id,
      data = RepositoryUserData(
        email = user.email,
        apiKey = user.apiKey,
        sessionToken = user.sessionToken,
        deviceToken = user.deviceToken,
        marketToken = user.marketToken,
        name = user.userProfile.name,
        avatar = user.userProfile.avatar,
        cover = user.userProfile.cover,
        deviceName = user.deviceName,
        deviceCloudId = user.deviceCloudId))

  def toRepositoryUserData(user: UserData): RepositoryUserData =
    RepositoryUserData(
      email = user.email,
      apiKey = user.apiKey,
      sessionToken = user.sessionToken,
      deviceToken = user.deviceToken,
      marketToken = user.marketToken,
      name = user.userProfile.name,
      avatar = user.userProfile.avatar,
      cover = user.userProfile.cover,
      deviceName = user.deviceName,
      deviceCloudId = user.deviceCloudId)
}
