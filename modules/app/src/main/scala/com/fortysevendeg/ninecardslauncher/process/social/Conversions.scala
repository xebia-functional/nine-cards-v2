package cards.nine.process.social

import cards.nine.services.persistence.UpdateUserRequest
import cards.nine.services.persistence.models.{User => ServicesUser}
import cards.nine.services.plus.models.GooglePlusProfile
trait Conversions {

  def toUpdateRequest(user: ServicesUser, googlePlusProfile: GooglePlusProfile) =
    UpdateUserRequest(
      id = user.id,
      email = user.email,
      apiKey = user.apiKey,
      sessionToken = user.sessionToken,
      deviceToken = user.deviceToken,
      marketToken = user.marketToken,
      name = googlePlusProfile.name,
      avatar = googlePlusProfile.avatarUrl,
      cover = googlePlusProfile.coverUrl,
      deviceName = user.deviceName,
      deviceCloudId = user.deviceCloudId)

}
