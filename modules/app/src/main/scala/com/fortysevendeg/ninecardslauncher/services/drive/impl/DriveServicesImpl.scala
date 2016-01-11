package com.fortysevendeg.ninecardslauncher.services.drive.impl

import java.io.{InputStream, OutputStreamWriter}

import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.drive.impl.Extensions._
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile
import com.fortysevendeg.ninecardslauncher.services.drive.{Conversions, DriveServiceException, DriveServices}
import com.google.android.gms.common.api.{GoogleApiClient, PendingResult, Result}
import com.google.android.gms.drive._
import com.google.android.gms.drive.metadata.CustomPropertyKey
import com.google.android.gms.drive.query.{Filters, Query}
import rapture.core
import rapture.core.{Answer, Errata}

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}
import scalaz.concurrent.Task

class DriveServicesImpl(client: GoogleApiClient)
  extends DriveServices
  with Conversions {

  private[this] val customFileType = "FILE_TYPE"

  private[this] def propertyKey = new CustomPropertyKey(customFileType, CustomPropertyKey.PRIVATE)

  def listFiles(maybeFileType: Option[String]) = Service {
    Task {
      val builder = new Query.Builder()
      maybeFileType foreach(fileType => builder.addFilter(Filters.eq(propertyKey, fileType)))
      val query = builder.build()

      Drive.DriveApi
        .getAppFolder(client)
        .queryChildren(client, query)
        .withResult { r =>
          Answer(r.getMetadataBuffer.iterator().toSeq map toGoogleDriveFile)
        }
    }
  }

  def readFile(file: DriveServiceFile) =
    openDriveFile(file) { driveContentsResult =>
      val contents = driveContentsResult.getDriveContents
      Answer(scala.io.Source.fromInputStream(contents.getInputStream).mkString)
    }

  def openFile(file: DriveServiceFile) =
    openDriveFile(file) { driveContentsResult =>
      val contents = driveContentsResult.getDriveContents
      Answer(contents.getInputStream)
    }

  def createFile(title: String, content: String, fileType: String, mimeType: String) =
    for {
      file <- createNewFile(title, fileType, mimeType)
      update <- updateFile(file, content)
    } yield update

  def createFile(title: String, content: InputStream, fileType: String, mimeType: String) =
    for {
      file <- createNewFile(title, fileType, mimeType)
      update <- updateFile(file, content)
    } yield update

  def updateFile(file: DriveServiceFile, content: String) =
    updateFile(file, _.write(content))

  def updateFile(file: DriveServiceFile, content: InputStream) =
    updateFile(
      file,
      writer => Iterator
        .continually(content.read)
        .takeWhile(_ != -1)
        .foreach(writer.write))

  private[this] def createNewFile(title: String, fileType: String, mimeType: String) = Service {
    Task {
      Drive.DriveApi
        .newDriveContents(client)
        .withResult { r =>
          val changeSet = new MetadataChangeSet.Builder()
            .setTitle(title)
            .setMimeType(mimeType)
            .setCustomProperty(propertyKey, fileType)
            .build()

          Drive.DriveApi
            .getAppFolder(client)
            .createFile(client, changeSet, r.getDriveContents)
            .withResult(nr => Answer(toGoogleDriveFile(title, nr.getDriveFile)))
        }

    }
  }

  private[this] def updateFile(file: DriveServiceFile, f: (OutputStreamWriter) => Unit) =
    openDriveFile(file) { driveContentsResult =>
      val contents = driveContentsResult.getDriveContents
      val writer = new OutputStreamWriter(contents.getOutputStream)
      f(writer)
      writer.close()
      contents.commit(client, javaNull).withResult(_ => Answer())
    }

  private[this] def openDriveFile[R](file: DriveServiceFile)(f: (DriveApi.DriveContentsResult) => core.Result[R, DriveServiceException]) = Service {
    Task {
      Drive.DriveApi
        .fetchDriveId(client, file.driveId)
        .withResult { result =>
          result
            .getDriveId.asDriveFile()
            .open(client, DriveFile.MODE_READ_ONLY, javaNull)
            .withResult(f(_))
        }
    }
  }

}

object Extensions {

  implicit class PendingResultOps[T <: Result](pendingResult: PendingResult[T]) {

    def withResult[R](f: (T) => core.Result[R, DriveServiceException]): core.Result[R, DriveServiceException] = {
      val result = pendingResult.await()
      if (result.getStatus.isSuccess) {
        Try(f(result)) match {
          case Success(r) => r
          case Failure(e) => Errata(DriveServiceException(e.getMessage, cause = Some(e)))
        }
      } else {
        Errata(DriveServiceException(
          statusCode = Some(result.getStatus.getStatusCode),
          message = result.getStatus.getStatusMessage))
      }
    }

  }

}
