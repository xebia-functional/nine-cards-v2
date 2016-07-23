package com.fortysevendeg.ninecardslauncher.services.drive.impl

import java.io.{InputStream, OutputStreamWriter}

import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.drive._
import com.fortysevendeg.ninecardslauncher.services.drive.impl.DriveServicesImpl._
import com.fortysevendeg.ninecardslauncher.services.drive.impl.Extensions._
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile
import com.google.android.gms.common.api.{CommonStatusCodes, GoogleApiClient, PendingResult, Result}
import com.google.android.gms.drive._
import com.google.android.gms.drive.metadata.CustomPropertyKey
import com.google.android.gms.drive.query.{Filters, Query, SortOrder, SortableField}
import rapture.core
import rapture.core.{Answer, Errata}

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}
import scalaz.concurrent.Task
import scalaz.Scalaz._

class DriveServicesImpl(client: GoogleApiClient)
  extends DriveServices
  with Conversions {

  private[this] val fileNotFoundError = (fileId: String) => s"File with id $fileId doesn't exists"

  private[this] val queryUUID = (fileId: String) => new Query.Builder()
    .addFilter(Filters.eq(propertyUUID, fileId))
    .build()

  override def listFiles(maybeFileType: Option[String]) = {
    val sortOrder = new SortOrder.Builder()
      .addSortAscending(SortableField.MODIFIED_DATE)
      .build()
    val maybeQuery = maybeFileType map { fileType =>
      new Query.Builder()
        .addFilter(Filters.eq(propertyFileType, fileType))
        .setSortOrder(sortOrder)
        .build()
    }
    searchFiles(maybeQuery)(seq => seq)
  }

  override def fileExists(driveId: String) =
    searchFileByUUID(driveId)(_.nonEmpty)

  override def readFile(driveId: String) =
    openDriveFile(driveId) { driveContentsResult =>
      val contents = driveContentsResult.getDriveContents
      val stringContent = scala.io.Source.fromInputStream(contents.getInputStream).mkString
      contents.discard(client)
      Answer(stringContent)
    }

  override def createFile(title: String, content: String, fileId: String, fileType: String, mimeType: String) =
    createNewFile(newUUID, title, fileId, fileType, mimeType, _.write(content))

  override def createFile(title: String, content: InputStream, fileId: String, fileType: String, mimeType: String) =
    createNewFile(newUUID, title, fileId, fileType, mimeType,
        writer => Iterator
          .continually(content.read)
          .takeWhile(_ != -1)
          .foreach(writer.write))

  override def updateFile(driveId: String, content: String) =
    updateFile(driveId, _.write(content))

  override def updateFile(driveId: String, content: InputStream) =
    updateFile(
      driveId,
      writer => Iterator
        .continually(content.read)
        .takeWhile(_ != -1)
        .foreach(writer.write))

  override def deleteFile(driveId: String) =
    fetchDriveFile(driveId)(_.delete(client).withResult(_ => Answer(Unit)))

  private[this] def newUUID = com.gilt.timeuuid.TimeUuid().toString

  private[this] def searchFileByUUID[R](driveId: String)(f: (Option[DriveServiceFile]) => R) = {
    val query = new Query.Builder()
      .addFilter(Filters.eq(propertyUUID, driveId))
      .build()
    searchFiles(query.some)(seq => f(seq.headOption))
  }

  private[this] def searchFiles[R](query: Option[Query])(f: (Seq[DriveServiceFile]) => R) = Service {
    Task {
      val request = query match {
        case Some(q) => appFolder.queryChildren(client, q)
        case _ => appFolder.listChildren(client)
      }
      request.withResult { r =>
        val buffer = r.getMetadataBuffer

        // Fix for actual devices using Google Drive
        val (validFiles, filesToFix) = buffer.iterator().toIterable.toList.partition { metadata =>
          Option(metadata.getCustomProperties.get(propertyUUID)).nonEmpty
        }
        android.util.Log.i("9Cards", s"Fixing ${filesToFix.size} Drive files")
        val fixedFiles = filesToFix map { metadata =>
          val uuid = newUUID
          val changeSet = new MetadataChangeSet.Builder()
            .setCustomProperty(propertyUUID, uuid)
            .build()
          metadata.getDriveId
            .asDriveResource()
            .updateMetadata(client, changeSet)
            .await()
          toGoogleDriveFile(uuid, metadata)
        }
        // End fix

        val response = f((validFiles map toGoogleDriveFile) ++ fixedFiles)
        buffer.release()
        Answer(response)
      }
    }
  }

  private[this] def appFolder = Drive.DriveApi.getAppFolder(client)

  private[this] def createNewFile(
    uuid: String,
    title: String,
    fileId: String,
    fileType: String,
    mimeType: String,
    f: (OutputStreamWriter) => Unit) = Service {
    Task {
      Drive.DriveApi
        .newDriveContents(client)
        .withResult { r =>
          val changeSet = new MetadataChangeSet.Builder()
            .setTitle(title)
            .setMimeType(mimeType)
            .setCustomProperty(propertyUUID, uuid)
            .setCustomProperty(propertyFileId, fileId)
            .setCustomProperty(propertyFileType, fileType)
            .build()

          val driveContents = r.getDriveContents
          val writer = new OutputStreamWriter(driveContents.getOutputStream)
          f(writer)
          writer.close()

          appFolder
            .createFile(client, changeSet, driveContents)
            .withResult { nr =>
              Answer(uuid)
            }
        }

    }
  }

  private[this] def updateFile(driveId: String, f: (OutputStreamWriter) => Unit) =
    openDriveFile(driveId, DriveFile.MODE_WRITE_ONLY) { driveContentsResult =>
      val contents = driveContentsResult.getDriveContents
      val writer = new OutputStreamWriter(contents.getOutputStream)
      f(writer)
      writer.close()
      contents.commit(client, javaNull).withResult(_ => Answer())
    }

  private[this] def fetchDriveFile[R](driveId: String)(f: (DriveFile) => core.Result[R, DriveServicesException]) =
    Service {
      Task {
        appFolder
          .queryChildren(client, queryUUID(driveId))
          .withResult { r =>
            val buffer = r.getMetadataBuffer
            val response = buffer.iterator().toIterable.headOption match {
              case Some(metaData) => f(metaData.getDriveId.asDriveFile)
              case None => Errata(DriveServicesException(fileNotFoundError(driveId)))
            }
            buffer.release()
            response
          }
      }
    }

  private[this] def openDriveFile[R](
    driveId: String,
    mode: Int = DriveFile.MODE_READ_ONLY)(f: (DriveApi.DriveContentsResult) => core.Result[R, DriveServicesException]) =
      fetchDriveFile(driveId)(_.open(client, mode, javaNull).withResult(f(_)))

}

object DriveServicesImpl {

  private[this] val uuid = "FILE_UUID"

  private[this] val customFileType = "FILE_TYPE"

  private[this] val customFileId = "FILE_ID"

  def propertyUUID = new CustomPropertyKey(uuid, CustomPropertyKey.PRIVATE)

  def propertyFileType = new CustomPropertyKey(customFileType, CustomPropertyKey.PRIVATE)

  def propertyFileId = new CustomPropertyKey(customFileId, CustomPropertyKey.PRIVATE)

}

object Extensions {

  implicit class PendingResultOps[T <: Result](pendingResult: PendingResult[T]) {

    def withResult[R](f: (T) => core.Result[R, DriveServicesException]): core.Result[R, DriveServicesException] =
      withResult(f, None)

    def withResult[R](
      f: (T) => core.Result[R, DriveServicesException],
      validCodesAndDefault: Option[(Seq[Int],R)]): core.Result[R, DriveServicesException] =
      (fetchResult, validCodesAndDefault) match {
        case (Some(result), _) if result.getStatus.isSuccess =>
          Try(f(result)) match {
            case Success(r) => r
            case Failure(e) => Errata(DriveServicesException(e.getMessage, cause = Some(e)))
          }
        case (Some(result), Some((validCodes, defaultValue))) if validCodes contains result.getStatus.getStatusCode =>
          Answer[R, DriveServicesException](defaultValue)
        case (Some(result), _) =>
          Errata(DriveServicesException(
            googleDriveError = statusCodeToError(result.getStatus.getStatusCode),
            message = result.getStatus.getStatusMessage))
        case _ =>
          Errata(DriveServicesException(
            message = "Received a null reference in pending result",
            cause = new NullPointerException().some))
      }

    private[this] def fetchResult: Option[T] = Option(pendingResult) map (_.await())

    private[this] def statusCodeToError(statusCode: Int) = statusCode match {
      case CommonStatusCodes.SIGN_IN_REQUIRED => DriveSigInRequired.some
      case DriveStatusCodes.DRIVE_RATE_LIMIT_EXCEEDED => DriveRateLimitExceeded.some
      case DriveStatusCodes.DRIVE_RESOURCE_NOT_AVAILABLE => DriveResourceNotAvailable.some
      case _ => None
    }

  }

}
