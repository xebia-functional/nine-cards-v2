package com.fortysevendeg.ninecardslauncher.services.drive.impl

import java.io.{InputStream, OutputStreamWriter}

import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.drive.{DriveRateLimitExceeded, DriveResourceNotAvailable, DriveSigInRequired}
import com.fortysevendeg.ninecardslauncher.services.drive.impl.DriveServicesImpl._
import com.fortysevendeg.ninecardslauncher.services.drive.impl.Extensions._
import com.fortysevendeg.ninecardslauncher.services.drive.{Conversions, DriveServices, DriveServicesException}
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

  def listFiles(maybeFileType: Option[String]) = {
    val sortOrder = new SortOrder.Builder()
      .addSortAscending(SortableField.MODIFIED_DATE)
      .build()
    val maybeQuery = maybeFileType map { fileType =>
      new Query.Builder()
        .addFilter(Filters.eq(propertyFileType, fileType))
        .setSortOrder(sortOrder)
        .build()
    }
    searchFiles(maybeQuery)(_ map toGoogleDriveFile)
  }

  def findFile(fileId: String) = {
    val query = new Query.Builder()
      .addFilter(Filters.eq(propertyFileId, fileId))
      .build()
    searchFiles(query.some)(_.headOption map toGoogleDriveFile)
  }

  def readFile(driveId: String) =
    openDriveFile(driveId) { driveContentsResult =>
      val contents = driveContentsResult.getDriveContents
      val stringContent = scala.io.Source.fromInputStream(contents.getInputStream).mkString
      contents.discard(client)
      Answer(stringContent)
    }

  def createFile(title: String, content: String, fileId: String, fileType: String, mimeType: String) =
    createNewFile(title, fileId, fileType, mimeType, _.write(content)) map (_ => ())

  def createFile(title: String, content: InputStream, fileId: String, fileType: String, mimeType: String) =
    createNewFile(title, fileId, fileType, mimeType,
        writer => Iterator
          .continually(content.read)
          .takeWhile(_ != -1)
          .foreach(writer.write)) map (_ => ())

  def updateFile(driveId: String, content: String) =
    updateFile(driveId, _.write(content))

  def updateFile(driveId: String, content: InputStream) =
    updateFile(
      driveId,
      writer => Iterator
        .continually(content.read)
        .takeWhile(_ != -1)
        .foreach(writer.write))

  def deleteFile(driveId: String) = Service {
    Task {
      Drive.DriveApi
        .fetchDriveId(client, driveId)
        .withResult { result =>
          result
            .getDriveId.asDriveFile()
            .delete(client)
            .withResult(_ => Answer(Unit))
        }
    }
  }

  private[this] def searchFiles[R](query: Option[Query])(f: (Seq[Metadata]) => R) = Service {
    Task {
      val request = query match {
        case Some(q) => appFolder.queryChildren(client, q)
        case _ => appFolder.listChildren(client)
      }
      request.withResult { r =>
        val buffer = r.getMetadataBuffer
        val response = f(buffer.iterator().toIterable.toList)
        buffer.release()
        Answer(response)
      }
    }
  }

  private[this] def appFolder = Drive.DriveApi.getAppFolder(client)

  private[this] def createNewFile(title: String, fileId: String, fileType: String, mimeType: String, f: (OutputStreamWriter) => Unit) = Service {
    Task {
      Drive.DriveApi
        .newDriveContents(client)
        .withResult { r =>
          val changeSet = new MetadataChangeSet.Builder()
            .setTitle(title)
            .setMimeType(mimeType)
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
              Answer(nr.getDriveFile)
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

  private[this] def openDriveFile[R](driveId: String, mode: Int = DriveFile.MODE_READ_ONLY)(f: (DriveApi.DriveContentsResult) => core.Result[R, DriveServicesException]) = Service {
    Task {
      Drive.DriveApi
        .fetchDriveId(client, driveId)
        .withResult { result =>
          result
            .getDriveId.asDriveFile()
            .open(client, mode, javaNull)
            .withResult(f(_))
        }
    }
  }

}

object DriveServicesImpl {

  private[this] val customFileType = "FILE_TYPE"

  private[this] val customFileId = "FILE_ID"

  def propertyFileType = new CustomPropertyKey(customFileType, CustomPropertyKey.PRIVATE)

  def propertyFileId = new CustomPropertyKey(customFileId, CustomPropertyKey.PRIVATE)
}

object Extensions {

  implicit class PendingResultOps[T <: Result](pendingResult: PendingResult[T]) {

    def withResult[R](f: (T) => core.Result[R, DriveServicesException]): core.Result[R, DriveServicesException] =
      fetchResult match {
        case Some(result) if result.getStatus.isSuccess =>
          Try(f(result)) match {
            case Success(r) => r
            case Failure(e) => Errata(DriveServicesException(e.getMessage, cause = Some(e)))
          }
        case Some(result) =>
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
