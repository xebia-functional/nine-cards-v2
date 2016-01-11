package com.fortysevendeg.ninecardslauncher.services.drive

import java.io.InputStream

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.drive.models.GoogleDriveFile

trait GoogleDriveServices {

  /**
    * Return a sequence of files in the user app space filtered by fileType key
    * @param maybeFileType any String or `None` to list all files
    * @return Sequence of `GoogleDriveFile`
    * @throws GoogleDriveException if there was an error with the request GoogleDrive api
    */
  def listFiles(maybeFileType: Option[String]): ServiceDef2[Seq[GoogleDriveFile], GoogleDriveException]

  /**
    * Returns the content of a file
    * @param file `GoogleDriveFile` that identifies the file to be read
    * @return the file content as String
    * @throws GoogleDriveException if there was an error with the request GoogleDrive api
    */
  def readFile(file: GoogleDriveFile): ServiceDef2[String, GoogleDriveException]

  /**
    * Creates a new text file
    * @param title the file title
    * @param content the content as String
    * @param fileType a String that later can be used in #listFiles method
    * @param mimeType the file mimeType
    * @throws GoogleDriveException if there was an error with the request GoogleDrive api
    */
  def createFile(title: String, content: String, fileType: String, mimeType: String): ServiceDef2[Unit, GoogleDriveException]

  /**
    * Creates a new file
    * @param title the file title
    * @param content the content as InputStream (won't be closed after finish)
    * @param fileType a String that later can be used in #listFiles method
    * @param mimeType the file mimeType
    * @throws GoogleDriveException if there was an error with the request GoogleDrive api
    */
  def createFile(title: String, content: InputStream, fileType: String, mimeType: String): ServiceDef2[Unit, GoogleDriveException]

  /**
    * Updates the content of an existing text file
    * @param file `GoogleDriveFile` that identifies the file to be updated
    * @param content the content as String
    * @throws GoogleDriveException if there was an error with the request GoogleDrive api
    */
  def updateFile(file: GoogleDriveFile, content: String): ServiceDef2[Unit, GoogleDriveException]

  /**
    * Updates the content of an existing file
    * @param file `GoogleDriveFile` that identifies the file to be updated
    * @param content the content as InputStream (won't be closed after finish)
    * @throws GoogleDriveException if there was an error with the request GoogleDrive api
    */
  def updateFile(file: GoogleDriveFile, content: InputStream): ServiceDef2[Unit, GoogleDriveException]

}
