package com.fortysevendeg.ninecardslauncher.process.user

import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.services.api.{InstallationResponse, LoginResponse}
import com.fortysevendeg.ninecardslauncher.services.api.models.GoogleDevice
import com.fortysevendeg.ninecardslauncher.services.persistence.UpdateUserRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User

trait Conversions {

  def toGoogleDevice(device: Device): GoogleDevice =
    GoogleDevice(
      name = device.name,
      deviceId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def toUpdateRequest(id: Int, email: String, user: User, login: LoginResponse, device: Device) =
    UpdateUserRequest(
      id = id,
      userId = login.user.id,
      email = Some(email),
      sessionToken = login.user.sessionToken,
      installationId = user.installationId,
      deviceToken = user.deviceToken,
      androidToken = Some(device.secretToken))

  def toUpdateRequest(id: Int, user: User, response: InstallationResponse) =
    UpdateUserRequest(
      id = id,
      userId = user.userId,
      email = user.email,
      sessionToken = user.sessionToken,
      installationId = response.installation.id,
      deviceToken = response.installation.deviceToken,
      androidToken = user.androidToken)

}
