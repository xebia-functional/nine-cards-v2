package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{User => RepositoryUser, UserData => RepositoryUserData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User

trait UserConversions {

  def toUser(user: RepositoryUser): User =
    User(
      id = user.id,
      email = user.data.email,
      apiKey = user.data.apiKey,
      sessionToken = user.data.sessionToken,
      deviceToken = user.data.deviceToken,
      marketToken = user.data.marketToken,
      name = user.data.name,
      avatar = user.data.avatar,
      cover = user.data.cover,
      deviceName = user.data.deviceName,
      deviceCloudId = user.data.deviceCloudId)

  def toRepositoryUser(user: User): RepositoryUser =
    RepositoryUser(
      id = user.id,
      data = RepositoryUserData(
        email = user.email,
        apiKey = user.apiKey,
        sessionToken = user.sessionToken,
        deviceToken = user.deviceToken,
        marketToken = user.marketToken,
        name = user.name,
        avatar = user.avatar,
        cover = user.cover,
        deviceName = user.deviceName,
        deviceCloudId = user.deviceCloudId))

  def toRepositoryUser(request: UpdateUserRequest): RepositoryUser =
    RepositoryUser(
      id = request.id,
      data = RepositoryUserData(
        email = request.email,
        apiKey = request.apiKey,
        sessionToken = request.sessionToken,
        deviceToken = request.deviceToken,
        marketToken = request.marketToken,
        name = request.name,
        avatar = request.avatar,
        cover = request.cover,
        deviceName = request.deviceName,
        deviceCloudId = request.deviceCloudId))

  def toRepositoryUserData(request: AddUserRequest): RepositoryUserData =
    RepositoryUserData(
      email = request.email,
      apiKey = request.apiKey,
      sessionToken = request.sessionToken,
      deviceToken = request.deviceToken,
      marketToken = request.marketToken,
      name = request.name,
      avatar = request.avatar,
      cover = request.cover,
      deviceName = request.deviceName,
      deviceCloudId = request.deviceCloudId)
}
