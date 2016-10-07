package cards.nine.services.persistence.conversions

import cards.nine.models.{UserData, User}
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

  def toRepositoryUserData(user: UserData): RepositoryUserData =
    RepositoryUserData(
      email = user.email,
      apiKey = user.apiKey,
      sessionToken = user.sessionToken,
      deviceToken = user.deviceToken,
      marketToken = user.marketToken,
      name = user.name,
      avatar = user.avatar,
      cover = user.cover,
      deviceName = user.deviceName,
      deviceCloudId = user.deviceCloudId)
}
