package com.fortysevendeg.ninecardslauncher.process.cloud.impl

import java.util.Date

import android.os.{Build, Bundle}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.{ActivityContextSupport, ContextSupport}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageImplicits._
import com.fortysevendeg.ninecardslauncher.process.cloud.models._
import com.fortysevendeg.ninecardslauncher.process.cloud._
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFileSummary
import com.fortysevendeg.ninecardslauncher.services.drive.DriveServices
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindUserByIdRequest, PersistenceServices}
import monix.eval.Task
import play.api.libs.json.Json
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.Drive

import scala.util.{Failure, Success, Try}

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

  override def createCloudStorageClient(account: String)(implicit contextSupport: ContextSupport) =
    TaskService {
      contextSupport.getOriginal.get match {
        case Some(activity: CloudStorageClientListener) =>
          CatchAll[CloudStorageProcessException] {
            new GoogleApiClient.Builder(contextSupport.context)
              .setAccountName(account)
              .addApi(Drive.API)
              .addScope(Drive.SCOPE_APPFOLDER)
              .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks {
                override def onConnectionSuspended(cause: Int): Unit =
                  activity.onDriveConnectionSuspended(cause)

                override def onConnected(bundle: Bundle): Unit =
                  activity.onDriveConnected()
              })
              .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener {
                override def onConnectionFailed(connectionResult: ConnectionResult): Unit =
                  activity.onDriveConnectionFailed(connectionResult)
              })
              .build()
          }
        case Some(_) =>
          Task(Left(CloudStorageProcessException("The implicit activity is not a CloudStorageClientListener")))
        case None =>
          Task(Left(CloudStorageProcessException("The implicit activity is null")))
      }
    }

  override def prepareForActualDevice[T <: CloudStorageResource](
    client: GoogleApiClient,
    devices: Seq[T])(implicit context: ContextSupport) = {

    def sort(devices: Seq[T]): Seq[T] =
      devices.sortBy(_.modifiedDate)(Ordering[Date].reverse)

    def fixAndSort(androidId: String, devices: Seq[T]): (Option[T], Seq[T]) = {

      val (userDevices, otherDevices) = devices.partition(_.deviceId.contains(androidId))

      val sortedUserDevices = sort(userDevices)

      val fixedUserDevice = sortedUserDevices.drop(1)

      (sortedUserDevices.headOption, sort(fixedUserDevice ++ otherDevices))
    }

    (for {
      androidId <- persistenceServices.getAndroidId
      fixedDevices = fixAndSort(androidId, devices)
    } yield fixedDevices).resolve[CloudStorageProcessException]
  }

  override def getCloudStorageDevices(client: GoogleApiClient)(implicit context: ContextSupport) =
    context.getActiveUserId map { id =>
      (for {
        driveServicesSeq <- driveServices.listFiles(client, Option(userDeviceType))
        maybeCloudId <- findUserDeviceCloudId(id)
      } yield driveServicesSeq map (file => toCloudStorageDeviceSummary(file, maybeCloudId))).resolve[CloudStorageProcessException]
    } getOrElse {
      TaskService(Task(Either.left(CloudStorageProcessException(noActiveUserErrorMessage))))
    }

  override def getCloudStorageDevice(
    client: GoogleApiClient,
    cloudId: String) = {

    def parseDevice(json: String): TaskService[CloudStorageDeviceData] = TaskService {
      Task {
        Try(Json.parse(json).as[CloudStorageDeviceData]) match {
          case Success(s) => Right(s)
          case Failure(e) => Left(CloudStorageProcessException(message = e.getMessage, cause = Option(e)))
        }
      }
    }

    (for {
      driveFile <- driveServices.readFile(client, cloudId)
      device <- parseDevice(driveFile.content)
    } yield CloudStorageDevice(
      cloudId = cloudId,
      createdDate = driveFile.summary.createdDate,
      modifiedDate = driveFile.summary.modifiedDate,
      data = device)).resolve[CloudStorageProcessException]
  }

  override def getRawCloudStorageDevice(
    client: GoogleApiClient,
    cloudId: String) =
    driveServices.readFile(client, cloudId).map { driveFile =>
      RawCloudStorageDevice(
        cloudId = cloudId,
        uuid = driveFile.summary.uuid,
        deviceId = driveFile.summary.deviceId,
        title = driveFile.summary.title,
        createdDate = driveFile.summary.createdDate,
        modifiedDate = driveFile.summary.modifiedDate,
        json = driveFile.content)
    }.resolve[CloudStorageProcessException]

  override def createOrUpdateCloudStorageDevice(
    client: GoogleApiClient,
    maybeCloudId: Option[String], cloudStorageDeviceData: CloudStorageDeviceData) =
    createOrUpdate(client, maybeCloudId, cloudStorageDeviceData)

  override def createOrUpdateActualCloudStorageDevice(
    client: GoogleApiClient,
    collections: Seq[CloudStorageCollection],
    moments: Seq[CloudStorageMoment],
    dockApps: Seq[CloudStorageDockApp])(implicit context: ContextSupport) = {

    def deviceExists(
      maybeCloudId: Option[String]): TaskService[Boolean] =
      maybeCloudId match {
        case Some(cloudId) =>
          driveServices.fileExists(client, cloudId)
        case _ =>
          TaskService(Task(Either.right(false)))
      }

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
          moments = Some(moments),
          dockApps = Some(dockApps))
        device <- createOrUpdate(
          client = client,
          maybeCloudId = if (exists) maybeCloudId else None,
          cloudStorageDeviceData = cloudStorageDeviceData)
      } yield device).resolve[CloudStorageProcessException]
    } getOrElse {
      TaskService(Task(Either.left(CloudStorageProcessException(noActiveUserErrorMessage))))
    }
  }

  override def deleteCloudStorageDevice(
    client: GoogleApiClient,
    cloudId: String) =
    driveServices.deleteFile(client, cloudId).resolve[CloudStorageProcessException]

  private[this] def createOrUpdate(
    client: GoogleApiClient,
    maybeCloudId: Option[String],
    cloudStorageDeviceData: CloudStorageDeviceData): TaskService[CloudStorageDevice] = {

    def createOrUpdateFile(
      maybeCloudId: Option[String],
      title: String,
      content: String,
      deviceId: String): TaskService[DriveServiceFileSummary] =
      maybeCloudId match {
        case Some(cloudId) => driveServices.updateFile(client, cloudId, title, content)
        case _ => driveServices.createFile(client, title, content, deviceId, userDeviceType, jsonMimeType)
      }

    def deviceToJson(device: CloudStorageDeviceData): TaskService[String] = TaskService {
      Task {
        Try(Json.toJson(device).toString()) match {
          case Success(s) => Either.right(s)
          case Failure(e) => Either.left(CloudStorageProcessException(message = e.getMessage, cause = Some(e)))
        }
      }
    }

    (for {
      json <- deviceToJson(cloudStorageDeviceData)
      summary <- createOrUpdateFile(maybeCloudId, cloudStorageDeviceData.deviceName, json, cloudStorageDeviceData.deviceId)
    } yield {
      CloudStorageDevice(
        cloudId = summary.uuid,
        createdDate = summary.createdDate,
        modifiedDate = summary.modifiedDate,
        data = cloudStorageDeviceData)
    }).resolve[CloudStorageProcessException]
  }

  private[this] def findUserDeviceCloudId(userId: Int): TaskService[Option[String]] = TaskService {
    persistenceServices.findUserById(FindUserByIdRequest(userId)).value map {
      case Right(Some(user)) => Right(user.deviceCloudId)
      case Right(None) => Left(CloudStorageProcessException(userNotFoundErrorMessage(userId)))
      case Left(e) => Either.left(CloudStorageProcessException(e.getMessage, Some(e)))
    }
  }

}