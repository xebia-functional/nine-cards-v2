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

package cards.nine.repository.user

import cards.nine.repository.model.{User, UserData}
import cards.nine.repository.provider.{UserEntity, UserEntityData}

import scala.util.Random

trait UserRepositoryTestData {

  val testId            = Random.nextInt(10)
  val testNonExistingId = 15
  val testEmail         = Random.nextString(10)
  val testApiKey        = Random.nextString(10)
  val testSessionToken  = Random.nextString(10)
  val testDeviceToken   = Random.nextString(10)
  val testAndroidToken  = Random.nextString(10)
  val testName          = Random.nextString(10)
  val testAvatar        = Random.nextString(10)
  val testCover         = Random.nextString(10)
  val testDeviceName    = Random.nextString(10)
  val testDeviceCloudId = Random.nextString(10)
  val testMockWhere     = "mock-where"

  val userEntitySeq = createUserEntitySeq(5)
  val userEntity    = userEntitySeq.head
  val userSeq       = createUserSeq(5)
  val user          = userSeq.head

  def createUserEntitySeq(num: Int) =
    List.tabulate(num)(
      i =>
        UserEntity(
          id = testId + i,
          data = UserEntityData(
            email = testEmail,
            apiKey = testApiKey,
            sessionToken = testSessionToken,
            deviceToken = testDeviceToken,
            marketToken = testAndroidToken,
            name = testName,
            avatar = testAvatar,
            cover = testCover,
            deviceName = testDeviceName,
            deviceCloudId = testDeviceCloudId)))

  def createUserSeq(num: Int) =
    List.tabulate(num)(
      i =>
        User(
          id = testId + i,
          data = UserData(
            email = Option(testEmail),
            apiKey = Option(testApiKey),
            sessionToken = Option(testSessionToken),
            deviceToken = Option(testDeviceToken),
            marketToken = Option(testAndroidToken),
            name = Option(testName),
            avatar = Option(testAvatar),
            cover = Option(testCover),
            deviceName = Option(testDeviceName),
            deviceCloudId = Option(testDeviceCloudId))))

  def createUserValues =
    Map[String, Any](
      UserEntity.email         -> testEmail,
      UserEntity.apiKey        -> testApiKey,
      UserEntity.sessionToken  -> testSessionToken,
      UserEntity.deviceToken   -> testDeviceToken,
      UserEntity.marketToken   -> testAndroidToken,
      UserEntity.name          -> testName,
      UserEntity.avatar        -> testAvatar,
      UserEntity.cover         -> testCover,
      UserEntity.deviceName    -> testDeviceName,
      UserEntity.deviceCloudId -> testDeviceCloudId)

  def createUserData =
    UserData(
      email = Option(testEmail),
      apiKey = Option(testApiKey),
      sessionToken = Option(testSessionToken),
      deviceToken = Option(testDeviceToken),
      marketToken = Option(testAndroidToken),
      name = Option(testName),
      avatar = Option(testAvatar),
      cover = Option(testCover),
      deviceName = Option(testDeviceName),
      deviceCloudId = Option(testDeviceCloudId))
}
