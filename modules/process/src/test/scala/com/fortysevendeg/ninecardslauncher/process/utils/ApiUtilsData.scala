package com.fortysevendeg.ninecardslauncher.process.utils

import com.fortysevendeg.ninecardslauncher.services.persistence.models.User

trait ApiUtilsData {

  val androidId = "012354654894654654"

  val token = "Session token"

  val userDBId = 1

  val user = User(
    id = userDBId,
    email = None,
    apiKey = None,
    sessionToken = Some(token),
    deviceToken = None,
    marketToken = None,
    name = None,
    avatar = None,
    cover = None,
    deviceName = None,
    deviceCloudId = None)

  val userSessionTokenNone = User(
    id = userDBId,
    email = None,
    apiKey = None,
    sessionToken = None,
    deviceToken = None,
    marketToken = None,
    name = None,
    avatar = None,
    cover = None,
    deviceName = None,
    deviceCloudId = None)

}
