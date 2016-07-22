package com.fortysevendeg.ninecardslauncher.process.cloud.impl

import android.os.Build
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageImplicits._
import com.fortysevendeg.ninecardslauncher.process.cloud.models._
import com.fortysevendeg.ninecardslauncher.process.cloud.{CloudStorageProcess, CloudStorageProcessException, Conversions, ImplicitsCloudStorageProcessExceptions}
import com.fortysevendeg.ninecardslauncher.services.drive.{DriveServices, DriveServicesException}
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServices}
import play.api.libs.json.Json
import rapture.core.{Answer, Errata, Result, Unforeseen}

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}
import scalaz.Scalaz._
import scalaz.concurrent.Task

class CloudStorageProcessImpl(
  driveServices: DriveServices,
  persistenceServices: PersistenceServices)
  extends CloudStorageProcess
  with ImplicitsCloudStorageProcessExceptions {

  import Conversions._

  private[this] val userDeviceType = "USER_DEVICE"

  private[this] val jsonMimeType = "application/json"

  private[this] val noActiveUserErrorMessage = "No active user"

  private[this] val userNotFoundErrorMessage = (id: Int) => s"User with id $id not found in the database"

  override def getCloudStorageDevices(implicit context: ContextSupport) =
    context.getActiveUserId map { id =>
      (for {
        driveServicesSeq <- driveServices.listFiles(userDeviceType.some)
        maybeCloudId <- findUserDeviceCloudId(id)
      } yield driveServicesSeq map (file => toCloudStorageDeviceSummary(file, maybeCloudId))).resolve[CloudStorageProcessException]
    } getOrElse {
      Service(Task(Result.errata[Seq[CloudStorageDeviceSummary], CloudStorageProcessException](CloudStorageProcessException(noActiveUserErrorMessage))))
    }

  override def getCloudStorageDevice(cloudId: String) = (for {
    json <- driveServices.readFile(cloudId)
    device <- parseDevice(json)
  } yield CloudStorageDevice(cloudId, device)).resolve[CloudStorageProcessException]

  override def createCloudStorageDevice(cloudStorageDeviceData: CloudStorageDeviceData) =
    createOrUpdateCloudStorageDevice(None, cloudStorageDeviceData)

  override def createOrUpdateActualCloudStorageDevice(
    collections: Seq[CloudStorageCollection],
    moments: Seq[CloudStorageMoment])(implicit context: ContextSupport) =
    context.getActiveUserId map { id =>
      (for {
        androidId <- persistenceServices.getAndroidId
        maybeCloudId <- findUserDeviceCloudId(id)
        exists <- deviceExists(maybeCloudId)
        cloudStorageDeviceData = CloudStorageDeviceData(
          deviceId = androidId,
          deviceName = Build.MODEL,
          documentVersion = CloudStorageProcess.actualDocumentVersion,
          collections = collections,
          moments = Some(moments))
        _ <- createOrUpdateCloudStorageDevice(
          maybeCloudId = if (exists) maybeCloudId else None,
          cloudStorageDeviceData = cloudStorageDeviceData)
      } yield ()).resolve[CloudStorageProcessException]
    } getOrElse {
      Service(Task(Result.errata[Unit, CloudStorageProcessException](CloudStorageProcessException(noActiveUserErrorMessage))))
    }

  override def deleteCloudStorageDevice(cloudId: String) =
    driveServices.deleteFile(cloudId).resolve[CloudStorageProcessException]

  private[this] def parseDevice(json: String): ServiceDef2[CloudStorageDeviceData, CloudStorageProcessException] = Service {
    Task {
      Try(Json.parse(json).as[CloudStorageDeviceData]) match {
        case Success(s) => Answer(s)
        case Failure(e) => Errata(CloudStorageProcessException(message = e.getMessage, cause = e.some))
      }
    }
  }

  private[this] def deviceToJson(device: CloudStorageDeviceData): ServiceDef2[String, CloudStorageProcessException] = Service {
    Task {
      Try(Json.toJson(device).toString()) match {
        case Success(s) => Answer(s)
        case Failure(e) => Errata(CloudStorageProcessException(message = e.getMessage, cause = Some(e)))
      }
    }
  }

  private[this] def createOrUpdateFile(
    maybeCloudId: Option[String],
    title: String,
    content: String,
    fileId: String): ServiceDef2[String, DriveServicesException] =
    maybeCloudId match {
      case Some(cloudId) => driveServices.updateFile(cloudId, content) map (_ => cloudId)
      case _ => driveServices.createFile(title, content, fileId, userDeviceType, jsonMimeType)
    }

  private[this] def deviceExists(
    maybeCloudId: Option[String]): ServiceDef2[Boolean, DriveServicesException] =
    maybeCloudId match {
      case Some(cloudId) =>
        driveServices.fileExists(cloudId)
      case _ =>
        Service(Task(Result.answer[Boolean, DriveServicesException](false)))
    }

  private[this] def createOrUpdateCloudStorageDevice(
    maybeCloudId: Option[String],
    cloudStorageDeviceData: CloudStorageDeviceData): ServiceDef2[CloudStorageDevice, CloudStorageProcessException] =
    (for {
      json <- deviceToJson(cloudStorageDeviceData)
      cloudId <- createOrUpdateFile(maybeCloudId, cloudStorageDeviceData.deviceName, json, cloudStorageDeviceData.deviceId)
    } yield CloudStorageDevice(cloudId, cloudStorageDeviceData)).resolve[CloudStorageProcessException]

  private[this] def findUserDeviceCloudId(userId: Int): ServiceDef2[Option[String], CloudStorageProcessException] = Service {
    persistenceServices.findUserById(FindUserByIdRequest(userId)).run map {
      case Answer(Some(user)) => Result.answer[Option[String], CloudStorageProcessException](user.deviceCloudId)
      case Answer(None) => Result.errata[Option[String], CloudStorageProcessException](CloudStorageProcessException(userNotFoundErrorMessage(userId)))
      case e@Errata(_) =>
        val exs = e.exceptions map (ie => (implicitly[ClassTag[CloudStorageProcessException]], (ie.getMessage, cloudStorageExceptionConverter(ie))))
        Errata[Option[String], CloudStorageProcessException](exs)
      case Unforeseen(ex) => Result.errata[Option[String], CloudStorageProcessException](CloudStorageProcessException("", Some(ex)))
    }
  }

}