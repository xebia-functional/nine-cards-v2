package com.fortysevendeg.ninecardslauncher.process.utils

import com.fortysevendeg.ninecardslauncher.services.persistence.models.User

trait ApiUtilsData {

  val androidId = "012354654894654654"

  val token = "Session token"

  val userDBId = 1

  val user = User(
    id = userDBId,
    userId = None,
    email = None,
    sessionToken = Some(token),
    installationId = None,
    deviceToken = None,
    androidToken = None
  )

  val userSessionTokenNone = User(
    id = userDBId,
    userId = None,
    email = None,
    sessionToken = None,
    installationId = None,
    deviceToken = None,
    androidToken = None
  )

}
