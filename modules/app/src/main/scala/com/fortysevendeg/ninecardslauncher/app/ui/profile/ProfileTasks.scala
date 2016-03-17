package com.fortysevendeg.ninecardslauncher.app.ui.profile

import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.AccountSync
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageDeviceSummary
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.device.DockAppException
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient

trait ProfileTasks {

  self: ProfileActivity =>

  def loadUserEmail(): ServiceDef2[Option[String], UserException] = di.userProcess.getUser.map(_.email)

  def loadAccounts(client: GoogleApiClient, email: String): ServiceDef2[Seq[AccountSync], CloudStorageProcessException] =  {
    val cloudStorageProcess = di.createCloudStorageProcess(client, email)
    cloudStorageProcess.getCloudStorageDevices.map(devices => createSync(devices))
  }

  def logout(): ServiceDef2[Unit, CollectionException with DockAppException with UserException] =
    for {
      _ <- di.collectionProcess.cleanCollections()
      _ <- di.deviceProcess.deleteAllDockApps
      _ <- di.userProcess.unregister
    } yield (())

  private[this] def createSync(devices: Seq[CloudStorageDeviceSummary]): Seq[AccountSync] = {
    val currentDevice = devices.find(_.currentDevice) map { d =>
      AccountSync.syncDevice(title = d.title, syncDate = d.modifiedDate, current = true)
    }
    val otherDevices = devices.filterNot(_.currentDevice) map { d =>
      AccountSync.syncDevice(title = d.title, syncDate = d.modifiedDate)
    }
    val otherDevicesWithHeader = if (otherDevices.isEmpty) {
      Seq.empty
    } else {
      AccountSync.header(getString(R.string.syncHeaderDevices)) +:
        otherDevices
    }
    (AccountSync.header(getString(R.string.syncCurrent)) +:
      currentDevice.toSeq) ++ otherDevicesWithHeader
  }

}
