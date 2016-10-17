package cards.nine.process.user.impl

import cards.nine.services.api.{LoginResponse, UpdateInstallationResponse}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait UserProcessData
  extends Scope
  with Mockito {

  val statusCodeOk = 200

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

  val updateInstallationResponse = UpdateInstallationResponse(statusCodeOk)

  val loginResponse = LoginResponse(statusCodeOk, apiKey, sessionToken)

}
