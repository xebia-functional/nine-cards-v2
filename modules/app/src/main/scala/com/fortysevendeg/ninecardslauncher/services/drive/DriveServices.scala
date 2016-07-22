package com.fortysevendeg.ninecardslauncher.services.drive

import java.io.InputStream

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile

trait DriveServices {

  /**
    * Return a sequence of files in the user app space filtered by fileType key
    * @param maybeFileType any String or `None` to list all files
    * @return Sequence of `DriveServiceFile`
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def listFiles(maybeFileType: Option[String]): ServiceDef2[Seq[DriveServiceFile], DriveServicesException]

  /**
    * Verify if a specific file exists
    * @param driveId file identifier
    * @return boolean indicating if the file exists
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def fileExists(driveId: String): ServiceDef2[Boolean, DriveServicesException]

  /**
    * Returns the content of a file
    * @param driveId that identifies the file to be read
    * @return the file content as String
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def readFile(driveId: String): ServiceDef2[String, DriveServicesException]

  /**
    * Creates a new text file
    * @param title the file title
    * @param content the content as String
    * @param fileId custom file identifier that can be used in #findFile method
    * @param fileType a String that later can be used in #listFiles method
    * @param mimeType the file
    * @return the file identifier
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def createFile(title: String, content: String, fileId: String, fileType: String, mimeType: String): ServiceDef2[String, DriveServicesException]

  /**
    * Creates a new file
    * @param title the file title
    * @param content the content as InputStream (won't be closed after finish)
    * @param fileType a String that later can be used in #listFiles method
    * @param mimeType the file mimeType
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def createFile(title: String, content: InputStream, fileId: String, fileType: String, mimeType: String): ServiceDef2[String, DriveServicesException]

  /**
    * Updates the content of an existing text file
    * @param driveId that identifies the file to be updated
    * @param content the content as String
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def updateFile(driveId: String, content: String): ServiceDef2[Unit, DriveServicesException]

  /**
    * Updates the content of an existing file
    * @param driveId that identifies the file to be updated
    * @param content the content as InputStream (won't be closed after finish)
    * @throws DriveServicesException if there was an error with the request GoogleDrive api
    */
  def updateFile(driveId: String, content: InputStream): ServiceDef2[Unit, DriveServicesException]

  /**
    * Try to delete the file
    * @param driveId that identifies the file to be updated
    * @return Unit
    * @throws DriveServicesException if there was an error or there is no file with this identifier
    */
  def deleteFile(driveId: String): ServiceDef2[Unit, DriveServicesException]

}
