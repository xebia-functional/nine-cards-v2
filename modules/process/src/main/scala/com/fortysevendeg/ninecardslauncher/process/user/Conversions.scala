package com.fortysevendeg.ninecardslauncher.process.user

import com.fortysevendeg.ninecardslauncher.process.user.models.{Device, User, UserProfile}
import com.fortysevendeg.ninecardslauncher.services.api.models.GoogleDevice
import com.fortysevendeg.ninecardslauncher.services.api.{InstallationResponse, LoginResponse}
import com.fortysevendeg.ninecardslauncher.services.persistence.UpdateUserRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{User => ServicesUser}

trait Conversions {

  def toGoogleDevice(device: Device): GoogleDevice =
    GoogleDevice(
      name = device.name,
      deviceId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def toUpdateRequest(id: Int, email: String, user: ServicesUser, login: LoginResponse, device: Device) =
    UpdateUserRequest(
      id = id,
      userId = login.user.id,
      email = Some(email),
      sessionToken = login.user.sessionToken,
      installationId = user.installationId,
      deviceToken = user.deviceToken,
      androidToken = Some(device.secretToken),
      name = user.name,
      avatar = user.avatar,
      cover = user.cover,
      deviceName = Some(device.name),
      deviceCloudId = None)

  def toUpdateRequest(id: Int, user: ServicesUser, response: InstallationResponse) =
    UpdateUserRequest(
      id = id,
      userId = user.userId,
      email = user.email,
      sessionToken = user.sessionToken,
      installationId = response.installation.id,
      deviceToken = response.installation.deviceToken,
      androidToken = user.androidToken,
      name = user.name,
      avatar = user.avatar,
      cover = user.cover,
      deviceName = user.deviceName,
      deviceCloudId = user.deviceCloudId)

  def toUpdateRequest(id: Int, user: ServicesUser, deviceName: String, deviceCloudId: String) =
    UpdateUserRequest(
      id = id,
      userId = user.userId,
      email = user.email,
      sessionToken = user.sessionToken,
      installationId = user.installationId,
      deviceToken = user.deviceToken,
      androidToken = user.androidToken,
      name = user.name,
      avatar = user.avatar,
      cover = user.cover,
      deviceName = Some(deviceName),
      deviceCloudId = Some(deviceCloudId))

  def toUser(user: ServicesUser): User =
    User(
      id = user.id,
      userId = user.userId,
      email = user.email,
      sessionToken = user.sessionToken,
      installationId = user.installationId,
      deviceToken = user.deviceToken,
      androidToken = user.androidToken,
      deviceName = user.deviceName,
      deviceCloudId = user.deviceCloudId,
      userProfile = toUserProfile(user))

  def toUserProfile(user: ServicesUser): UserProfile =
    UserProfile(user.name, user.avatar, user.cover)

}
