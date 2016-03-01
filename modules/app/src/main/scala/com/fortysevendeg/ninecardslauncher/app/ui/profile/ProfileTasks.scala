package com.fortysevendeg.ninecardslauncher.app.ui.profile

import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.AccountSync
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageDeviceSummary
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient

trait ProfileTasks {

  self: ProfileActivity =>

  def loadUserEmail(): ServiceDef2[Option[String], UserException] = di.userProcess.getUser.map(_.email)

  def loadAccounts(client: GoogleApiClient, email: String): ServiceDef2[Seq[AccountSync], CloudStorageProcessException] =  {
    val cloudStorageProcess = di.createCloudStorageProcess(client, email)
    cloudStorageProcess.getCloudStorageDevices.map(devices => createSync(devices))
  }

  private[this] def createSync(devices: Seq[CloudStorageDeviceSummary]): Seq[AccountSync] = {
    val currentDevice = devices.find(_.currentDevice) map { d =>
      AccountSync.syncDevice(title = d.title, d.modifiedDate)
    }
    ((AccountSync.header(getString(R.string.syncHeaderDevices)) +:
      createSyncDevices(devices)) :+
      AccountSync.header(getString(R.string.syncHeaderSynchronize))) ++
      currentDevice.toSeq
  }

  private[this] def createSyncDevices(devices: Seq[CloudStorageDeviceSummary]): Seq[AccountSync] =
    devices map { device =>
      AccountSync.device(
        title = device.title,
        current = device.currentDevice)
    }

}
