package cards.nine.process.user

import cards.nine.models.{User => ServicesUser}
import cards.nine.process.user.models.{User, UserProfile}

trait Conversions {
//
//  def toUpdateRequest(id: Int, user: ServicesUser) =
//    UpdateUserRequest(
//      id = id,
//      email = user.email,
//      apiKey = user.apiKey,
//      sessionToken = user.sessionToken,
//      deviceToken = user.deviceToken,
//      marketToken = user.marketToken,
//      name = user.name,
//      avatar = user.avatar,
//      cover = user.cover,
//      deviceName = user.deviceName,
//      deviceCloudId = user.deviceCloudId)

  def toUser(user: ServicesUser): User =
    User(
      id = user.id,
      email = user.email,
      apiKey = user.apiKey,
      sessionToken = user.sessionToken,
      deviceToken = user.deviceToken,
      marketToken = user.marketToken,
      deviceName = user.deviceName,
      deviceCloudId = user.deviceCloudId,
      userProfile = toUserProfile(user))

  def toUserProfile(user: ServicesUser): UserProfile =
    UserProfile(user.name, user.avatar, user.cover)

}
