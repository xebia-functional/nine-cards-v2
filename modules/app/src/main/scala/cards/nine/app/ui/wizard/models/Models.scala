package cards.nine.app.ui.wizard.models

import java.util.Date

import cards.nine.models.UserV1Device

case class UserCloudDevices(
    deviceType: DeviceType,
    name: String,
    userDevice: Option[UserCloudDevice],
    devices: Seq[UserCloudDevice],
    dataV1: Seq[UserV1Device])

case class UserCloudDevice(
    deviceName: String,
    cloudId: String,
    currentDevice: Boolean,
    modifiedDate: Date)

sealed trait DeviceType

case object NoFoundDeviceType extends DeviceType

case object V1DeviceType extends DeviceType

case object GoogleDriveDeviceType extends DeviceType
