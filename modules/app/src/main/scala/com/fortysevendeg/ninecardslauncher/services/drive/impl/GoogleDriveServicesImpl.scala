package com.fortysevendeg.ninecardslauncher.services.drive.impl

import java.io.{InputStream, OutputStreamWriter}

import com.fortysevendeg.ninecardslauncher.services.drive.{Conversions, GoogleDriveException, GoogleDriveServices}
import com.fortysevendeg.ninecardslauncher.services.drive.impl.Extensions._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.drive.models.GoogleDriveFile
import com.google.android.gms.common.api.{GoogleApiClient, PendingResult, Result}
import com.google.android.gms.drive._
import com.google.android.gms.drive.metadata.CustomPropertyKey
import com.google.android.gms.drive.query.{Filters, Query}
import rapture.core
import rapture.core.{Answer, Errata}

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}
import scalaz.concurrent.Task

class GoogleDriveServicesImpl(client: GoogleApiClient)
  extends GoogleDriveServices
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

  def readFile(file: GoogleDriveFile) = Service {
    Task {
      file.driveFile
        .open(client, DriveFile.MODE_READ_ONLY, javaNull)
        .withResult { r =>
          val contents = r.getDriveContents
          Answer(scala.io.Source.fromInputStream(contents.getInputStream).mkString)
        }
    }
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

  def updateFile(file: GoogleDriveFile, content: String) =
    updateFile(file, _.write(content))

  def updateFile(file: GoogleDriveFile, content: InputStream) =
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
            .withResult(nr => Answer(GoogleDriveFile(title, nr.getDriveFile)))
        }

    }
  }

  private[this] def updateFile(file: GoogleDriveFile, f: (OutputStreamWriter) => Unit) = Service {
    Task {
      file.driveFile
        .open(client, DriveFile.MODE_WRITE_ONLY, javaNull)
        .withResult { r =>
          val contents = r.getDriveContents
          val writer = new OutputStreamWriter(contents.getOutputStream)
          f(writer)
          writer.close()
          contents.commit(client, javaNull).withResult(_ => Answer())
        }
    }
  }

}

object Extensions {

  implicit class PendingResultOps[T <: Result](pendingResult: PendingResult[T]) {

    def withResult[R](f: (T) => core.Result[R, GoogleDriveException]): core.Result[R, GoogleDriveException] = {
      val result = pendingResult.await()
      if (result.getStatus.isSuccess) {
        Try(f(result)) match {
          case Success(r) => r
          case Failure(e) => Errata(GoogleDriveException(e.getMessage, cause = Some(e)))
        }
      } else {
        Errata(GoogleDriveException(
          statusCode = Some(result.getStatus.getStatusCode),
          message = result.getStatus.getStatusMessage))
      }
    }

  }

}
