package com.fortysevendeg.ninecardslauncher.app.ui.wizard.models

import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageDevice

case class UserCloudDevices(name: String, devices: Seq[CloudStorageDevice])