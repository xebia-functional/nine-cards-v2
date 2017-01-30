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

package cards.nine.models

case class User(
    id: Int,
    email: Option[String],
    apiKey: Option[String],
    sessionToken: Option[String],
    deviceToken: Option[String],
    marketToken: Option[String],
    deviceName: Option[String],
    deviceCloudId: Option[String],
    userProfile: UserProfile)

case class UserData(
    email: Option[String],
    apiKey: Option[String],
    sessionToken: Option[String],
    deviceToken: Option[String],
    marketToken: Option[String],
    deviceName: Option[String],
    deviceCloudId: Option[String],
    userProfile: UserProfile)

case class UserProfile(name: Option[String], avatar: Option[String], cover: Option[String])

object User {

  implicit class UserOps(user: User) {

    def toData =
      UserData(
        email = user.email,
        apiKey = user.apiKey,
        sessionToken = user.sessionToken,
        deviceToken = user.deviceToken,
        marketToken = user.marketToken,
        deviceName = user.deviceName,
        deviceCloudId = user.deviceCloudId,
        userProfile = user.userProfile)

  }
}
