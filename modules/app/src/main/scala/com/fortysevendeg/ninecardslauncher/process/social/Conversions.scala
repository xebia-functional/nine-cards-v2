package com.fortysevendeg.ninecardslauncher.process.social

import com.fortysevendeg.ninecardslauncher.services.persistence.UpdateUserRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{User => ServicesUser}
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile
trait Conversions {

  def toUpdateRequest(user: ServicesUser, googlePlusProfile: GooglePlusProfile) =
    UpdateUserRequest(
      id = user.id,
      userId = user.userId,
      email = user.email,
      sessionToken = user.sessionToken,
      installationId = user.installationId,
      deviceToken = user.deviceToken,
      androidToken = user.androidToken,
      name = googlePlusProfile.name,
      avatar = googlePlusProfile.avatarUrl,
      cover = googlePlusProfile.coverUrl)

}
