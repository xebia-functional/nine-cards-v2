package com.fortysevendeg.ninecardslauncher.process.user

import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.services.api.models.GoogleDevice

trait Conversions {

  def toGoogleDevice(device: Device): GoogleDevice =
    GoogleDevice(
      name = device.name,
      deviceId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions
    )

}
