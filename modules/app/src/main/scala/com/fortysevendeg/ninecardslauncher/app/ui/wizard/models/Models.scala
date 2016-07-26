package com.fortysevendeg.ninecardslauncher.app.ui.wizard.models

import java.util.Date

case class UserPermissions(token: String, oauthScopes: Seq[String])

case class UserCloudDevices(name: String, userDevice: Option[UserCloudDevice], devices: Seq[UserCloudDevice])

case class UserCloudDevice(deviceName: String, cloudId: String, currentDevice: Boolean, fromV1: Boolean, modifiedDate: Date)