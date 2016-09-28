package com.fortysevendeg.ninecardslauncher.services.drive.impl

import java.io.{InputStream, OutputStreamWriter}

import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.services.drive._
import com.fortysevendeg.ninecardslauncher.services.drive.impl.DriveServicesImpl._
import com.fortysevendeg.ninecardslauncher.services.drive.impl.Extensions._
import com.fortysevendeg.ninecardslauncher.services.drive.models.{DriveServiceFile, DriveServiceFileSummary}
import com.google.android.gms.common.api._
import com.google.android.gms.drive._
import com.google.android.gms.drive.metadata.CustomPropertyKey
import com.google.android.gms.drive.query.{Filters, Query, SortOrder, SortableField}
import monix.eval.Task
import monix.execution.Cancelable

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

class DriveServicesImpl
  extends DriveServices
  with Conversions {

  private[this] val fileNotFoundError = (driveId: String) => s"File with id $driveId doesn't exists"

  private[this] val queryUUID = (driveId: String) => new Query.Builder()
    .addFilter(Filters.eq(propertyUUID, driveId))
    .build()

  override def createDriveClient(account: String)(implicit contextSupport: ContextSupport) =
    TaskService {
      Task {
        Either.catchNonFatal {
          new GoogleApiClient.Builder(contextSupport.context)
            .setAccountName(account)
            .addApi(Drive.API)
            .addScope(Drive.SCOPE_APPFOLDER)
            .build()
        } leftMap {
          e: Throwable => DriveServicesException(message = e.getMessage, googleDriveError = None, cause = Some(e))
        }
      }
    }

  override def listFiles(
    client: GoogleApiClient,
    maybeFileType: Option[String]) = {
    val sortOrder = new SortOrder.Builder()
      .addSortAscending(SortableField.MODIFIED_DATE)
      .build()
    val maybeQuery = maybeFileType map { fileType =>
      new Query.Builder()
        .addFilter(Filters.eq(propertyFileType, fileType))
        .setSortOrder(sortOrder)
        .build()
    }
    searchFiles(client, maybeQuery)(seq => seq)
  }

  override def fileExists(
    client: GoogleApiClient,
    driveId: String) =
    searchFiles(client, Option(queryUUID(driveId)))(_.nonEmpty)

  override def readFile(
    client: GoogleApiClient,
    driveId: String) = {

    def readFileContentsService(driveId: DriveId): TaskService[String] =
      driveId.asDriveFile
        .open(client, DriveFile.MODE_READ_ONLY, javaNull)
        .withResultAsync { result =>
          val contents = result.getDriveContents
          val stringContent = scala.io.Source.fromInputStream(contents.getInputStream).mkString
          contents.discard(client)
          Right(stringContent)
        }

    for {
      metadataResult <- fetchDriveFile(client, driveId)
      content <- readFileContentsService(metadataResult.metadata.getDriveId)
    } yield {
      val summary = toGoogleDriveFileSummary(metadataResult.metadata)
      tryToClose(metadataResult.buffer)
      DriveServiceFile(summary, content)
    }

  }

  override def createFile(
    client: GoogleApiClient,
    title: String, content: String, deviceId: String, fileType: String, mimeType: String) =
    createNewFile(client, newUUID, title, deviceId, fileType, mimeType, _.write(content))

  override def createFile(
    client: GoogleApiClient,
    title: String, content: InputStream, deviceId: String, fileType: String, mimeType: String) =
    createNewFile(client, newUUID, title, deviceId, fileType, mimeType,
      writer => Iterator
        .continually(content.read)
        .takeWhile(_ != -1)
        .foreach(writer.write))

  override def updateFile(
    client: GoogleApiClient,
    driveId: String, title: String, content: String) =
    updateFileWith(client, driveId, title, _.write(content))

  override def updateFile(
    client: GoogleApiClient,
    driveId: String, title: String, content: InputStream) =
    updateFileWith(
      client,
      driveId,
      title,
      writer => Iterator
        .continually(content.read)
        .takeWhile(_ != -1)
        .foreach(writer.write))

  override def deleteFile(
    client: GoogleApiClient,
    driveId: String) = {

    def deleteFileService(driveId: DriveId): TaskService[Unit] =
      driveId
        .asDriveFile
        .delete(client)
        .withResultAsync(_ => Right(Unit))

    for {
      metadataResult <- fetchDriveFile(client, driveId)
      _ <- deleteFileService(metadataResult.metadata.getDriveId)
      _ = tryToClose(metadataResult.buffer)
    } yield ()
  }

  private[this] def newUUID = com.gilt.timeuuid.TimeUuid().toString

  private[this] def searchFiles[R](
    client: GoogleApiClient,
    query: Option[Query])(f: (Seq[DriveServiceFileSummary]) => R) = {

    def searchFiles: TaskService[MetadataBuffer] = {
      val request = query match {
        case Some(q) => appFolder(client).queryChildren(client, q)
        case _ => appFolder(client).listChildren(client)
      }
      request.withResultAsync { result =>
        Right(result.getMetadataBuffer)
      }
    }

    def splitFiles(buffer: MetadataBuffer): (List[Metadata], List[Metadata]) =
      buffer.iterator().toIterable.toList.partition { metadata =>
        Option(metadata.getCustomProperties.get(propertyUUID)).nonEmpty
      }

    def fixUUID(files: List[Metadata]): TaskService[List[DriveServiceFileSummary]] = {
      /*
       * TODO - Remove this block as part of ticket 525 (https://github.com/47deg/nine-cards-v2/issues/525)
       * This code fixes actual devices using Google Drive
       */
      val tasks = files map { metadata =>
        val uuid = newUUID
        val changeSet = new MetadataChangeSet.Builder()
          .setCustomProperty(propertyUUID, uuid)
          .build()
        metadata.getDriveId
          .asDriveResource()
          .updateMetadata(client, changeSet)
          .withResultAsync { result =>
            Right(toGoogleDriveFileSummary(uuid, result.getMetadata).copy(uuid = uuid))
          }
          .value
      }

      TaskService {
        Task.gatherUnordered(tasks) map { list =>
          Right(list.collect {
            case Right(r) => r
          })
        }
      }
    }

    for {
      buffer <- searchFiles
      (validFiles, filesToFix) = splitFiles(buffer)
      fixedFiles <- fixUUID(filesToFix)
    } yield {
      val summaryFiles = validFiles map toGoogleDriveFileSummary
      tryToClose(buffer)
      f(summaryFiles ++ fixedFiles)
    }
  }

  private[this] def appFolder(client: GoogleApiClient) = Drive.DriveApi.getAppFolder(client)

  private[this] def createNewFile(
    client: GoogleApiClient,
    uuid: String,
    title: String,
    deviceId: String,
    fileType: String,
    mimeType: String,
    f: (OutputStreamWriter) => Unit): TaskService[DriveServiceFileSummary] = {

    def createNewFileService: TaskService[DriveContents] =
      Drive.DriveApi
        .newDriveContents(client)
        .withResultAsync(result => Right(result.getDriveContents))

    def writeContents(contents: DriveContents): TaskService[DriveServiceFileSummary] = {
      val changeSet = new MetadataChangeSet.Builder()
        .setTitle(title)
        .setMimeType(mimeType)
        .setCustomProperty(propertyUUID, uuid)
        .setCustomProperty(propertyDeviceId, deviceId)
        .setCustomProperty(propertyFileType, fileType)
        .build()

      val writer = new OutputStreamWriter(contents.getOutputStream)
      f(writer)
      writer.close()

      appFolder(client)
        .createFile(client, changeSet, contents)
        .withResultAsync { result =>
          val now = new java.util.Date
          Right(DriveServiceFileSummary(
            uuid = uuid,
            deviceId = Some(deviceId),
            title = title,
            createdDate = now,
            modifiedDate = now))
        }
    }

    for {
      contents <- createNewFileService
      summary <- writeContents(contents)
    } yield summary
  }

  private[this] def updateFileWith(
    client: GoogleApiClient,
    driveId: String, title: String, f: (OutputStreamWriter) => Unit): TaskService[DriveServiceFileSummary] = {

    def changeTitleService(driveId: DriveId): TaskService[Metadata] = {
      val changeSet = new MetadataChangeSet.Builder()
        .setTitle(title)
        .build()
      driveId
        .asDriveResource()
        .updateMetadata(client, changeSet)
        .withResultAsync(result => Right(result.getMetadata))
    }

    def writeContentsService(driveId: DriveId): TaskService[DriveContents] =
      driveId
        .asDriveFile
        .open(client, DriveFile.MODE_WRITE_ONLY, javaNull)
        .withResultAsync { result =>
          val contents = result.getDriveContents
          val writer = new OutputStreamWriter(contents.getOutputStream)
          f(writer)
          writer.close()
          Right(contents)
        }

    for {
      metadataResult <- fetchDriveFile(client, driveId)
      newMetadata <- changeTitleService(metadataResult.metadata.getDriveId)
      summary = toGoogleDriveFileSummary(newMetadata)
      contents <- writeContentsService(newMetadata.getDriveId)
      _ <- commitContentsService(client, contents)
    } yield {
      tryToClose(metadataResult.buffer)
      summary
    }
  }

  private[this] def tryToClose(metadata: MetadataBuffer): Unit = Try(metadata.release())

  private[this] def commitContentsService(
    client: GoogleApiClient,
    contents: DriveContents): TaskService[Unit] =
    contents.commit(client, javaNull).withResultAsync(_ => Right((): Unit))

  private[this] def fetchDriveFile(
    client: GoogleApiClient,
    driveId: String): TaskService[MetadataResult] =
    appFolder(client)
      .queryChildren(client, queryUUID(driveId))
      .withResultAsync { r =>
        val buffer = r.getMetadataBuffer
        val response = buffer.iterator().toIterable.headOption match {
          case Some(metaData) => Right(MetadataResult(buffer, metaData))
          case None =>
            buffer.release()
            Left(DriveServicesException(fileNotFoundError(driveId)))
        }
        response
      }

}

object DriveServicesImpl {

  private[this] val uuid = "FILE_UUID"

  private[this] val customFileType = "FILE_TYPE"

  private[this] val customDeviceId = "FILE_ID"

  def propertyUUID = new CustomPropertyKey(uuid, CustomPropertyKey.PRIVATE)

  def propertyFileType = new CustomPropertyKey(customFileType, CustomPropertyKey.PRIVATE)

  def propertyDeviceId = new CustomPropertyKey(customDeviceId, CustomPropertyKey.PRIVATE)

  case class MetadataResult(buffer: MetadataBuffer, metadata: Metadata)

}

object Extensions {

  implicit class PendingResultOps[T <: Result](pendingResult: PendingResult[T]) {

    def withResultAsync[R](f: (T) => Either[DriveServicesException, R]): TaskService[R] =
      withResultAsync(f, None)

    def withResultAsync[R](
      f: (T) => Either[DriveServicesException, R],
      validCodesAndDefault: Option[(Seq[Int], R)]): TaskService[R] = {

      def statusCodeToError(statusCode: Int) = statusCode match {
        case CommonStatusCodes.SIGN_IN_REQUIRED => Option(DriveSigInRequired)
        case DriveStatusCodes.DRIVE_RATE_LIMIT_EXCEEDED => Option(DriveRateLimitExceeded)
        case DriveStatusCodes.DRIVE_RESOURCE_NOT_AVAILABLE => Option(DriveResourceNotAvailable)
        case _ => None
      }

      TaskService {
        Task.async[DriveServicesException Either R] { (scheduler, callback) =>
          pendingResult.setResultCallback(new ResultCallback[T] {
            override def onResult(callbackResult: T): Unit = {
              (Option(callbackResult), validCodesAndDefault) match {
                case (Some(result), _) if result.getStatus.isSuccess =>
                  Try(f(result)) match {
                    case Success(r) => callback(Success(r))
                    case Failure(e) => callback(Success(Left(DriveServicesException(e.getMessage, cause = Some(e)))))
                  }
                case (Some(result), Some((validCodes, defaultValue))) if validCodes contains result.getStatus.getStatusCode =>
                  callback(Success(Right(defaultValue)))
                case (Some(result), _) =>
                  callback(Success(Left(DriveServicesException(
                    googleDriveError = statusCodeToError(result.getStatus.getStatusCode),
                    message = result.getStatus.getStatusMessage))))
                case _ =>
                  callback(Success(Left(DriveServicesException(
                    message = "Received a null reference in pending result",
                    cause = Option(new NullPointerException())))))
              }
            }
          })
          Cancelable.empty
        }
      }
    }

  }

}
