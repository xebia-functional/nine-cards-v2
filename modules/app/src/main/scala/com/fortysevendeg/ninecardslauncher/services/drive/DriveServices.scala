package com.fortysevendeg.ninecardslauncher.services.drive

import java.io.InputStream

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile

trait DriveServices {

  /**
    * Return a sequence of files in the user app space filtered by fileType key
    * @param maybeFileType any String or `None` to list all files
    * @return Sequence of `DriveServiceFile`
    * @throws DriveServiceException if there was an error with the request GoogleDrive api
    */
  def listFiles(maybeFileType: Option[String]): ServiceDef2[Seq[DriveServiceFile], DriveServiceException]

  /**
    * Try to fetch the file with the specified `fileId`
    * @param fileId custom file identifier
    * @return Option of `DriveServiceFile`
    * @throws DriveServiceException if there was an error with the request GoogleDrive api
    */
  def findFile(fileId: String): ServiceDef2[Option[DriveServiceFile], DriveServiceException]

  /**
    * Returns the content of a file
    * @param driveId that identifies the file to be read
    * @return the file content as String
    * @throws DriveServiceException if there was an error with the request GoogleDrive api
    */
  def readFile(driveId: String): ServiceDef2[String, DriveServiceException]

  /**
    * Returns the content of a file as InputStream
    * @param driveId that identifies the file to be read
    * @return the file content as String
    * @throws DriveServiceException if there was an error with the request GoogleDrive api
    */
  def openFile(driveId: String): ServiceDef2[InputStream, DriveServiceException]

  /**
    * Creates a new text file
    * @param title the file title
    * @param content the content as String
    * @param fileId custom file identifier that can be used in #findFile method
    * @param fileType a String that later can be used in #listFiles method
    * @param mimeType the file mimeType
    * @throws DriveServiceException if there was an error with the request GoogleDrive api
    */
  def createFile(title: String, content: String, fileId: String, fileType: String, mimeType: String): ServiceDef2[Unit, DriveServiceException]

  /**
    * Creates a new file
    * @param title the file title
    * @param content the content as InputStream (won't be closed after finish)
    * @param fileType a String that later can be used in #listFiles method
    * @param mimeType the file mimeType
    * @throws DriveServiceException if there was an error with the request GoogleDrive api
    */
  def createFile(title: String, content: InputStream, fileId: String, fileType: String, mimeType: String): ServiceDef2[Unit, DriveServiceException]

  /**
    * Updates the content of an existing text file
    * @param driveId that identifies the file to be updated
    * @param content the content as String
    * @throws DriveServiceException if there was an error with the request GoogleDrive api
    */
  def updateFile(driveId: String, content: String): ServiceDef2[Unit, DriveServiceException]

  /**
    * Updates the content of an existing file
    * @param driveId that identifies the file to be updated
    * @param content the content as InputStream (won't be closed after finish)
    * @throws DriveServiceException if there was an error with the request GoogleDrive api
    */
  def updateFile(driveId: String, content: InputStream): ServiceDef2[Unit, DriveServiceException]

}
