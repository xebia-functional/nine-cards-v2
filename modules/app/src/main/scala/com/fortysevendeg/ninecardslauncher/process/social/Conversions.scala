package com.fortysevendeg.ninecardslauncher.process.social

import com.fortysevendeg.ninecardslauncher.services.persistence.UpdateUserRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{User => ServicesUser}
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile
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
