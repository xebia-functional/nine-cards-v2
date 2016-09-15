package com.fortysevendeg.ninecardslauncher.services.drive

import java.io.InputStream

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.services.drive.models.{DriveServiceFile, DriveServiceFileSummary}

trait DriveServices {

  /**
    * Return a sequence of files in the user app space filtered by fileType key
    * @param maybeFileType any String or `None` to list all files
    * @return Sequence of `DriveServiceFile`
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def listFiles(maybeFileType: Option[String]): TaskService[Seq[DriveServiceFileSummary]]

  /**
    * Verify if a specific file exists
    * @param driveId file identifier
    * @return boolean indicating if the file exists
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def fileExists(driveId: String): TaskService[Boolean]

  /**
    * Returns the content of a file
    * @param driveId that identifies the file to be read
    * @return the file content as String
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def readFile(driveId: String): TaskService[DriveServiceFile]

  /**
    * Creates a new text file
    * @param title the file title
    * @param content the content as String
    * @param deviceId custom device identifier
    * @param fileType a String that later can be used in #listFiles method
    * @param mimeType the file
    * @return the file identifier
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def createFile(title: String, content: String, deviceId: String, fileType: String, mimeType: String): TaskService[DriveServiceFileSummary]

  /**
    * Creates a new file
    * @param title the file title
    * @param content the content as InputStream (won't be closed after finish)
    * @param deviceId custom device identifier
    * @param fileType a String that later can be used in #listFiles method
    * @param mimeType the file mimeType
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def createFile(title: String, content: InputStream, deviceId: String, fileType: String, mimeType: String): TaskService[DriveServiceFileSummary]

  /**
    * Updates the content of an existing text file
    * @param driveId that identifies the file to be updated
    * @param title the new title
    * @param content the content as String
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def updateFile(driveId: String, title: String, content: String): TaskService[DriveServiceFileSummary]

  /**
    * Updates the content of an existing file
    * @param driveId that identifies the file to be updated
    * @param title the new title
    * @param content the content as InputStream (won't be closed after finish)
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def updateFile(driveId: String, title: String, content: InputStream): TaskService[DriveServiceFileSummary]

  /**
    * Try to delete the file
    * @param driveId that identifies the file to be updated
    * @return Unit
    * @throws DriveServicesException if there was an error or there is no file with this identifier
    */
  def deleteFile(driveId: String): TaskService[Unit]

}
