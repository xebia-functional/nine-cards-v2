package com.fortysevendeg.ninecardslauncher.process.user.models

case class Device(
  name: String,
  deviceId: String,
  secretToken: String,
  permissions: Seq[String])
