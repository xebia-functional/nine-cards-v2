package com.fortysevendeg.ninecardslauncher.process.social.impl

import cards.nine.services.persistence.{FindUserByIdRequest, UpdateUserRequest}
import cards.nine.services.persistence.models.User
import cards.nine.services.plus.models.GooglePlusProfile

trait SocialProfileProcessImplData {

  val activeUserId = 10

  val name = "name"

  val avatarUrl = "avatarUrl"

  val coverUrl = "coverUrl"

  val account = "example@domain.com"

  val clientId = "fake-client-id"

  val googlePlusProfile = GooglePlusProfile(
    name = Some(name),
    avatarUrl = Some(avatarUrl),
    coverUrl = Some(coverUrl))

  val user = User(
    activeUserId,
    email = Some(account),
    apiKey = Some("api-key"),
    sessionToken = Some("session-token"),
    deviceToken = Some("device-token"),
    marketToken = Some("android-token"),
    name = Some(name),
    avatar = Some(avatarUrl),
    cover = Some(coverUrl),
    deviceName = Some("device"),
    deviceCloudId = Some("device-cloud-id"))

  val findUserByIdRequest = FindUserByIdRequest(activeUserId)

  val updateUserRequest = UpdateUserRequest(
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
