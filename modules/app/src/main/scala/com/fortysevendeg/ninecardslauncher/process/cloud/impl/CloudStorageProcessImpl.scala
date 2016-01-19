package com.fortysevendeg.ninecardslauncher.process.cloud.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageDevice
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageImplicits._
import com.fortysevendeg.ninecardslauncher.process.cloud.{CloudStorageProcess, CloudStorageProcessException, Conversions, ImplicitsCloudStorageProcessExceptions}
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile
import com.fortysevendeg.ninecardslauncher.services.drive.{DriveServices, DriveServicesException}
import play.api.libs.json.Json
import rapture.core.{Answer, Errata}

import scala.util.{Failure, Success, Try}
import scalaz.Scalaz._
import scalaz.concurrent.Task

class CloudStorageProcessImpl(driveServices: DriveServices)
  extends CloudStorageProcess
  with Conversions
  with ImplicitsCloudStorageProcessExceptions {


  private[this] val userDeviceType = "USER_DEVICE"

  private[this] val jsonMimeType = "application/json"

  override def getCloudStorageDevices() = (for {
    driveServicesSeq <- driveServices.listFiles(userDeviceType.some)
  } yield driveServicesSeq map toDriveDevice).resolve[CloudStorageProcessException]

  override def getCloudStorageDevice(cloudStorageResourceId: String) = (for {
    json <- driveServices.readFile(cloudStorageResourceId)
    device <- parseDevice(json)
  } yield device).resolve[CloudStorageProcessException]

  override def createOrUpdateCloudStorageDevice(cloudStorageDevice: CloudStorageDevice) = (for {
    file <- driveServices.findFile(cloudStorageDevice.deviceId)
    json <- deviceToJson(cloudStorageDevice)
    _ <- createOrUpdateFile(file, cloudStorageDevice.deviceName, json, cloudStorageDevice.deviceId)
  } yield ()).resolve[CloudStorageProcessException]

  private[this] def parseDevice(json: String): ServiceDef2[CloudStorageDevice, CloudStorageProcessException] = Service {
    Task {
      Try(Json.parse(json).as[CloudStorageDevice]) match {
        case Success(s) => Answer(s)
        case Failure(e) => Errata(CloudStorageProcessException(message = e.getMessage, cause = e.some))
      }
    }
  }

  private[this] def deviceToJson(device: CloudStorageDevice): ServiceDef2[String, CloudStorageProcessException] = Service {
    Task {
      Try(Json.toJson(device).toString()) match {
        case Success(s) => Answer(s)
        case Failure(e) => Errata(CloudStorageProcessException(message = e.getMessage, cause = Some(e)))
      }
    }
  }

  private[this] def createOrUpdateFile(
    maybeDriveFile: Option[DriveServiceFile],
    title: String,
    content: String,
    fileId: String): ServiceDef2[Unit, DriveServicesException] =
    maybeDriveFile match {
      case Some(driveFile) => driveServices.updateFile(driveFile.driveId, content)
      case _ => driveServices.createFile(title, content, fileId, userDeviceType, jsonMimeType)
    }
}
