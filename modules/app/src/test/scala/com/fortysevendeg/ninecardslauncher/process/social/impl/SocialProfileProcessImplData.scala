package com.fortysevendeg.ninecardslauncher.process.social.impl

import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, UpdateUserRequest}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User
import com.fortysevendeg.ninecardslauncher.services.plus.models.GooglePlusProfile

trait SocialProfileProcessImplData {

  val activeUserId = 10

  val name = "name"

  val avatarUrl = "avatarUrl"

  val coverUrl = "coverUrl"

  val googlePlusProfile = GooglePlusProfile(
    name = Some(name),
    avatarUrl = Some(avatarUrl),
    coverUrl = Some(coverUrl))

  val user = User(
    activeUserId,
    userId = Some("user-id"),
    email = Some("email"),
    sessionToken = Some("session-token"),
    installationId = Some("installation-id"),
    deviceToken = Some("device-token"),
    androidToken = Some("android-token"),
    name = Some(name),
    avatar = Some(avatarUrl),
    cover = Some(coverUrl))

  val findUserByIdRequest = FindUserByIdRequest(activeUserId)

  val updateUserRequest = UpdateUserRequest(
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
