package cards.nine.process.user.impl

import cards.nine.process.user.models.{User, UserProfile}
import cards.nine.services.api.{LoginResponse, UpdateInstallationResponse}
import cards.nine.services.persistence.{AddUserRequest, UpdateUserRequest}
import cards.nine.services.persistence.models.{User => ServicesUser}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

import scala.util.Random

trait UserProcessData
  extends Scope
  with Mockito {

  val statusCodeOk = 200

  val userId = 1

  val anotherUserId = 1

  val email = "example@47deg.com"

  val deviceName = "Nexus 47"
  val anotherDeviceName = "Another device"

  val deviceId = "XX-47-XX"

  val deviceCloudId = "fake-device-cloud-id"
  val anotherDeviceCloudId = "fake-device-cloud-id-2"

  val apiKey = "apiKey"
  val sessionToken = "sessionToken"
  val deviceToken = "deviceToken"
  val anotherDeviceToken = "anotherDeviceToken"
  val marketToken = "marketToken"
  val emailTokenId = "emailTokenId"

  val persistenceUser = ServicesUser(
    id = userId,
    email = Some(email),
    apiKey = None,
    sessionToken = None,
    deviceToken = Some(deviceToken),
    marketToken = Some(marketToken),
    name = Some(Random.nextString(10)),
    avatar = Some(Random.nextString(10)),
    cover = Some(Random.nextString(10)),
    deviceName = Some(deviceName),
    deviceCloudId = Some(deviceCloudId))

  val processUser = User(
    id = persistenceUser.id,
    email = persistenceUser.email,
    apiKey = persistenceUser.apiKey,
    sessionToken = persistenceUser.sessionToken,
    deviceToken = persistenceUser.deviceToken,
    marketToken = persistenceUser.marketToken,
    deviceName = persistenceUser.deviceName,
    deviceCloudId = persistenceUser.deviceCloudId,
    userProfile = UserProfile(
      name = persistenceUser.name,
      avatar = persistenceUser.avatar,
      cover = persistenceUser.cover))

  val anotherUser = ServicesUser(
    id = anotherUserId,
    email = None,
    apiKey = None,
    sessionToken = None,
    deviceToken = None,
    marketToken = None,
    name = Some(Random.nextString(10)),
    avatar = Some(Random.nextString(10)),
    cover = Some(Random.nextString(10)),
    deviceName = None,
    deviceCloudId = None)

  val emptyAddUserRequest = AddUserRequest(None, None, None, None, None, None, None, None, None, None)

  val emptyUpdateUserRequest = UpdateUserRequest(userId, None, None, None, None, None, None, None, None, None, None)

  val updateUserRequest = UpdateUserRequest(
    id = userId,
    email = persistenceUser.email,
    apiKey = persistenceUser.apiKey,
    sessionToken = persistenceUser.sessionToken,
    deviceToken = persistenceUser.deviceToken,
    marketToken = persistenceUser.marketToken,
    name = persistenceUser.name,
    avatar = persistenceUser.avatar,
    cover = persistenceUser.cover,
    deviceName = persistenceUser.deviceName,
    deviceCloudId = persistenceUser.deviceCloudId)

  val updateInstallationResponse = UpdateInstallationResponse(statusCodeOk)

  val loginResponse = LoginResponse(statusCodeOk, apiKey, sessionToken)

}
