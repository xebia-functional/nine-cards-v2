package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{User => RepositoryUser, UserData => RepositoryUserData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User

trait UserConversions {

  def toUserSeq(user: Seq[RepositoryUser]): Seq[User] = user map toUser

  def toUser(user: RepositoryUser): User =
    User(
      id = user.id,
      userId = user.data.userId,
      email = user.data.email,
      sessionToken = user.data.sessionToken,
      installationId = user.data.installationId,
      deviceToken = user.data.deviceToken,
      androidToken = user.data.androidToken,
      name = user.data.name,
      avatar = user.data.avatar,
      cover = user.data.cover,
      deviceName = user.data.deviceName,
      deviceCloudId = user.data.deviceCloudId)

  def toRepositoryUser(user: User): RepositoryUser =
    RepositoryUser(
      id = user.id,
      data = RepositoryUserData(
        userId = user.userId,
        email = user.email,
        sessionToken = user.sessionToken,
        installationId = user.installationId,
        deviceToken = user.deviceToken,
        androidToken = user.androidToken,
        name = user.name,
        avatar = user.avatar,
        cover = user.cover,
        deviceName = user.deviceName,
        deviceCloudId = user.deviceCloudId))

  def toRepositoryUser(request: UpdateUserRequest): RepositoryUser =
    RepositoryUser(
      id = request.id,
      data = RepositoryUserData(
        userId = request.userId,
        email = request.email,
        sessionToken = request.sessionToken,
        installationId = request.installationId,
        deviceToken = request.deviceToken,
        androidToken = request.androidToken,
        name = request.name,
        avatar = request.avatar,
        cover = request.cover,
        deviceName = request.deviceName,
        deviceCloudId = request.deviceCloudId))

  def toRepositoryUserData(request: AddUserRequest): RepositoryUserData =
    RepositoryUserData(
      userId = request.userId,
      email = request.email,
      sessionToken = request.sessionToken,
      installationId = request.installationId,
      deviceToken = request.deviceToken,
      androidToken = request.androidToken,
      name = request.name,
      avatar = request.avatar,
      cover = request.cover,
      deviceName = request.deviceName,
      deviceCloudId = request.deviceCloudId)
}
