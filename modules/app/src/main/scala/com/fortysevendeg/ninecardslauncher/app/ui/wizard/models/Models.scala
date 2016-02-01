package com.fortysevendeg.ninecardslauncher.app.ui.wizard.models

import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageDevice

case class UserPermissions(token: String, oauthScopes: Seq[String])

case class UserCloudDevices(name: String, devices: Seq[CloudStorageDevice])